package org.cmpe295.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.aspectj.apache.bcel.classfile.Utility;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

public class UserUtilityLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    LocalDate dateOfLink;
    LocalDate dateOfUnlink;
    private Boolean isActive;

    @JsonIgnoreProperties({"address","password"})
    @ManyToOne
    @JoinColumn(name="userId", nullable=false, updatable = true, insertable = true,referencedColumnName = "id")
    private User user;
    @ManyToOne
    @JoinColumn(name="utilityAccountNumber", nullable=false, updatable = true, insertable = true,referencedColumnName = "utilityAccountNumber")
    private UtilityAccount utilityAccount;

}
