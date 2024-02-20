package org.cmpe295.user.service;

import lombok.RequiredArgsConstructor;
import org.cmpe295.user.controller.UserController;
import org.cmpe295.user.entity.MeterReading;
import org.cmpe295.user.entity.User;
import org.cmpe295.user.entity.UtilityAccount;
import org.cmpe295.user.exceptions.UtilityAccountNotFoundException;
import org.cmpe295.user.model.UserDetailsResponse;
import org.cmpe295.user.model.UserUtilityAccountDetails;
import org.cmpe295.user.repository.MeterReadingRepository;
import org.cmpe295.user.repository.UserRepository;
import org.cmpe295.user.repository.UtilityAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UtilityAccountRepository utilityAccountRepository;
    @Autowired
    private MeterReadingRepository meterReadingRepository;

    /**
     * End point to return user - utility accoubt details
     * @param userName
     * @return
     */
    public UserDetailsResponse getUserDetails(String userName){
        UserDetailsResponse response = new UserDetailsResponse();
        // Use the findByEmail method to get the additional user details
        Optional<User> userOptional = userRepository.findByEmail(userName);
        if (userOptional.isPresent()) {
            logger.info("Found the user corresponding to the username/email id");
            User user = userOptional.get();
            response.setId(user.getId());
            response.setEmail(user.getEmail());
            response.setLastname(user.getLastName());
            response.setFirstname(user.getFirstName());
            response.setReadingValue(0);
            response.setDateOfReading(LocalDate.now());
            //Write methods to get this
            Optional<UserUtilityAccountDetails> utilityAccount = utilityAccountRepository.findFirstActiveUtilityAccountDetailsByUserId(user.getId());
            if(utilityAccount.isPresent()){
                UserUtilityAccountDetails userUtilityAccountDetails = utilityAccount.get();
                response.setCurrentUtilityAccountNumber(userUtilityAccountDetails.getUtilityAccount().getUtilityAccountNumber());
                logger.info("Found the latest active utility account linked to the user: "+ userUtilityAccountDetails.getUtilityAccount().getUtilityAccountNumber());
                response.setDateOfLink(userUtilityAccountDetails.getDateOfLink());
                Optional<MeterReading> meterReading = meterReadingRepository.findFirstByUtilityAccountOrderByDateOfReadingDescReadingIdDesc(
                        utilityAccountRepository.findByUtilityAccountNumber(userUtilityAccountDetails.getUtilityAccount().getUtilityAccountNumber()).get()
                );
                if(meterReading.isPresent()){
                    MeterReading latestReading = meterReading.get();
                    response.setReadingValue(latestReading.getReadingValue()!=null?latestReading.getReadingValue():0);
                    response.setDateOfReading(latestReading.getDateOfReading()!=null?latestReading.getDateOfReading():LocalDate.now());
                }
            }

        }else{
            logger.error("Couldn't find the user corresponding to username/email: "+userName);
            throw new IllegalArgumentException();
        }
        return  response;
    }

    /**
     * End point to return messages for the user
     * @param username
     * @return
     */
    public String generateMessages(String username) {
        StringBuilder messages = new StringBuilder();
        if (isBillAmountDue(username)) {
            messages.append("Your bill amount is due. ");
        }
        if (isMeterImageUploadDue(username)) {
            messages.append("Meter image upload is due. ");
        }
        if (hasErrorInMeterReading(username)) {
            messages.append("There is an error in predicting the meter reading. ");
        }
        return messages.toString();
    }
    private boolean isBillAmountDue(String username) {
        // ... logic to check if bill amount is due
        return false;
    }

    private boolean isMeterImageUploadDue(String userName) {
        // ... logic to check if meter image upload is due
        Optional<User> userOptional = userRepository.findByEmail(userName);
        if (userOptional.isPresent()) {
            Optional<UserUtilityAccountDetails> utilityAccount = utilityAccountRepository.findFirstActiveUtilityAccountDetailsByUserId(userOptional.get().getId());
            if(utilityAccount.isPresent()){
                UserUtilityAccountDetails userUtilityAccountDetails = utilityAccount.get();
                logger.info("Found the latest active utility account linked to the user: "+ userUtilityAccountDetails.getUtilityAccount().getUtilityAccountNumber());
                Optional<MeterReading> meterReading = meterReadingRepository.findFirstByUtilityAccountOrderByDateOfReadingDescReadingIdDesc(
                        utilityAccountRepository.findByUtilityAccountNumber(userUtilityAccountDetails.getUtilityAccount().getUtilityAccountNumber()).get()
                );
                if(meterReading.isPresent()){
                    MeterReading latestReading = meterReading.get();
                    LocalDate lastUploadDate = latestReading.getDateOfReading();
                    LocalDate currentDate = LocalDate.now();
                    return lastUploadDate.isBefore(currentDate.minusDays(30));
                }else{
                    return true;
                }
            }
        }else{
            throw new UsernameNotFoundException(userName);
        }
        return false;
    }

    private boolean hasErrorInMeterReading(String userName) {
        // ... logic to check if there is an error in predicting the meter reading
        Optional<User> userOptional = userRepository.findByEmail(userName);
        if (userOptional.isPresent()) {
            Optional<UserUtilityAccountDetails> utilityAccount = utilityAccountRepository.findFirstActiveUtilityAccountDetailsByUserId(userOptional.get().getId());
            if(utilityAccount.isPresent()){
                UserUtilityAccountDetails userUtilityAccountDetails = utilityAccount.get();
                logger.info("Found the latest active utility account linked to the user: "+ userUtilityAccountDetails.getUtilityAccount().getUtilityAccountNumber());
                Optional<MeterReading> meterReading = meterReadingRepository.findFirstByUtilityAccountOrderByDateOfReadingDescReadingIdDesc(
                        utilityAccountRepository.findByUtilityAccountNumber(userUtilityAccountDetails.getUtilityAccount().getUtilityAccountNumber()).get()
                );
                if(meterReading.isPresent()){
                    MeterReading latestReading = meterReading.get();
                    return latestReading.getReadingValue() == null;
                }
            }
        }else{
            throw new UsernameNotFoundException(userName);
        }
        return false;
    }
}
