package org.cmpe295.user.service;

import lombok.RequiredArgsConstructor;
import org.cmpe295.user.controller.UserController;
import org.cmpe295.user.entity.MeterReading;
import org.cmpe295.user.entity.User;
import org.cmpe295.user.entity.UtilityAccount;
import org.cmpe295.user.entity.enums.ACTION;
import org.cmpe295.user.entity.enums.METER_READING_ENTRY_STATUS;
import org.cmpe295.user.exceptions.UtilityAccountNotFoundException;
import org.cmpe295.user.model.MessageResponse;
import org.cmpe295.user.model.UpdateUserRequest;
import org.cmpe295.user.model.UserDetailsResponse;
import org.cmpe295.user.model.UserUtilityAccountDetails;
import org.cmpe295.user.repository.MeterReadingRepository;
import org.cmpe295.user.repository.UserRepository;
import org.cmpe295.user.repository.UtilityAccountRepository;
import org.cmpe295.user.security.service.JWTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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
    @Value("${reading.threshold.days}")
    private int readingThresholdDays;

    private final PasswordEncoder passwordEncoder;

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
            response.setReadingValue(String.valueOf(0));
            response.setDateOfReading(LocalDate.now().toString());
            //Write methods to get this
            Optional<UserUtilityAccountDetails> utilityAccount = utilityAccountRepository.findFirstActiveUtilityAccountDetailsByUserId(user.getId());
            if(utilityAccount.isPresent()){
                UserUtilityAccountDetails userUtilityAccountDetails = utilityAccount.get();
                response.setCurrentUtilityAccountNumber(userUtilityAccountDetails.getUtilityAccount().getUtilityAccountNumber());
                logger.info("Found the latest active utility account linked to the user: "+ userUtilityAccountDetails.getUtilityAccount().getUtilityAccountNumber());
                response.setDateOfLink(userUtilityAccountDetails.getDateOfLink().toString());
                Optional<MeterReading> meterReading = meterReadingRepository.findFirstByUtilityAccountOrderByDateOfReadingDescReadingIdDesc(
                        utilityAccountRepository.findByUtilityAccountNumber(userUtilityAccountDetails.getUtilityAccount().getUtilityAccountNumber()).get()
                );
                if(meterReading.isPresent()){
                    MeterReading latestReading = meterReading.get();
                    response.setReadingValue(latestReading.getReadingValue()!=null?String.valueOf(latestReading.getReadingValue()):String.valueOf(0));
                    response.setDateOfReading(latestReading.getDateOfReading()!=null?latestReading.getDateOfReading().toString():LocalDate.now().toString());
                }
            }

        }else{
            logger.error("Couldn't find the user corresponding to username/email: "+userName);
            throw new IllegalArgumentException();
        }
        return  response;
    }
    public UserDetailsResponse updateUserDetails(String userName, UpdateUserRequest updateUserRequest){
        UserDetailsResponse response = new UserDetailsResponse();
        try {
            Optional<User> userOptional = userRepository.findByEmail(userName);
            if (userOptional.isPresent()) {
                logger.info("Found the user corresponding to the username/email id");
                User user = userOptional.get();
                // Update only if the fields are provided in the request body
                if (updateUserRequest.getFirstname() != null) {
                    user.setFirstName(updateUserRequest.getFirstname());
                }
                if (updateUserRequest.getLastname() != null) {
                    user.setLastName(updateUserRequest.getLastname());
                }
                if (updateUserRequest.getPassword() != null) {
                    user.setPassword(passwordEncoder.encode(updateUserRequest.getPassword()));

                }
                final User updatedUser = userRepository.save(user);

                //Set the response
                response = getUserDetails(userName);

            } else {
                logger.error("Couldn't find the user corresponding to username/email: " + userName);
                throw new IllegalArgumentException();
            }
        }catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
        return response;
    }

    /**
     * New service method to generate messages based on the latest non-discarded meter reading entry for a user.
     * The following are the statuses possible for a meter reading entry
     *      PENDING_VERIFICATION,
     *      ERROR,
     *      PENDING_CONFIRMATION,
     *      DISCARDED,
     *      MANUAL_ENTRY,
     *      BILL_GENERATED
     * Given a user, we can find the utility account - Optional<UserUtilityAccountDetails> utilityAccount = utilityAccountRepository.findFirstActiveUtilityAccountDetailsByUserId(user.getId());
        - We get
     * Give a user and a utility account number, we can get the latest non-discarded meter reading entry like this
     * Optional<MeterReading> findFirstByUtilityAccountUtilityAccountNumberAndStatusNotOrderByDateOfReadingDesc(Long utilityAccountNumber, METER_READING_ENTRY_STATUS status);

     * @param userName
     * @return
     */
    public List<MessageResponse> generateMessagesForTheUser(String userName) {
        List<MessageResponse> messages = new ArrayList<>();
        Optional<User> userOptional = userRepository.findByEmail(userName);
        if (userOptional.isPresent()) {
            logger.info("Found the user corresponding to the username/email id");
            Optional<UserUtilityAccountDetails> utilityAccount = utilityAccountRepository.findFirstActiveUtilityAccountDetailsByUserId(userOptional.get().getId());
            //Find the last non-discarded meter reading entry for the utility account
            if(utilityAccount.isPresent()){
                Optional<MeterReading> lastReadingEntry = meterReadingRepository
                        .findFirstByUtilityAccountUtilityAccountNumberAndStatusNotOrderByDateOfReadingDesc(utilityAccount.get().getUtilityAccount().getUtilityAccountNumber()
                                , METER_READING_ENTRY_STATUS.DISCARDED);
                if(lastReadingEntry.isPresent()){
                    switch (lastReadingEntry.get().getStatus()){
                        case ERROR -> messages.add(new MessageResponse(ACTION.MANUAL_METER_READING, "There is an error in detecting the meter reading. Please help the system by manually entering the reading details"));
                        case PENDING_CONFIRMATION -> messages.add(new MessageResponse(ACTION.CONFIRM_AUTOMATED_METER_READING, "There is an error in detecting the meter reading. Please help the system by manually entering the reading details"));
                        case PENDING_VERIFICATION -> messages.add(new MessageResponse(ACTION.NO_ACTION,"Waiting for system to finalize the reading"));
                        case CONFIRMED -> messages.add(new MessageResponse(ACTION.BILL_AMOUNT_DUE, "Your bill amount is due. "));
                        case MANUAL_ENTRY -> messages.add(new MessageResponse(ACTION.BILL_AMOUNT_DUE, "Your bill amount is due. "));
                        case BILL_PAID -> {
                            //If the bill is paid, check the date of the reading. If it has been more than 30 days, a new meter image is due
                            if(checkReadingThreshold(lastReadingEntry.get().getDateOfReading())){
                                messages.add(new MessageResponse(ACTION.CAPTURE_IMAGE,"Meter image upload is due. "));
                            }else{
                                messages.add(new MessageResponse(ACTION.NO_ACTION,"No messages. Everything up to date."));
                            }
                        }
                    }
                }else{
                    messages.add(new MessageResponse(ACTION.CAPTURE_IMAGE,"Meter image upload is due. "));
                }
            }else{
                //Send message to update the address to add a utility account
                messages.add(new MessageResponse(ACTION.LINK_UTILITY_ACCOUNT, "Update your address to activate a utility account"));
            }
        }else{
            throw new UsernameNotFoundException(userName);
        }
        if(messages.size()==0)
            messages.add(new MessageResponse(ACTION.NO_ACTION,"No messages. Everything up to date"));
        return messages;
    }

    public boolean checkReadingThreshold(LocalDate dateOfReading) {
        LocalDate currentDate = LocalDate.now();
        long daysDifference = ChronoUnit.DAYS.between(dateOfReading, currentDate);
        if (daysDifference > readingThresholdDays) {
            return true;
        }
        return false;
    }

    /**
     * End point to return messages for the user
     * @param username
     * @return
     */
    public List<MessageResponse> generateMessages(String username) {
        List<MessageResponse> messages = new ArrayList<>();
        //StringBuilder messages = new StringBuilder();
        if (isBillAmountDue(username)) {
            messages.add(new MessageResponse(ACTION.BILL_AMOUNT_DUE, "Your bill amount is due. ") );
        }
        if (isMeterImageUploadDue(username)) {
            messages.add(new MessageResponse(ACTION.CAPTURE_IMAGE,"Meter image upload is due. "));
        }
        if (hasErrorInMeterReading(username)) {
            messages.add(new MessageResponse(ACTION.MANUAL_METER_READING,"There is an error in predicting the meter reading. "));
        }
        if(messages.size()==0)
            messages.add(new MessageResponse(ACTION.NO_ACTION,"No messages. Everything up to date"));
        return messages;
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
