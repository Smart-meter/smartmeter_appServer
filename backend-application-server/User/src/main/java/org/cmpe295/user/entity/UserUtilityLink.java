package org.cmpe295.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.apache.bcel.classfile.Utility;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserUtilityLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    LocalDate dateOfLink;
    LocalDate dateOfUnlink;
    private Boolean isActive;

    @JsonIgnoreProperties({"address","password"})
    @ManyToOne
    @JoinColumn(name="userId", nullable=false, updatable = false, insertable = false,referencedColumnName = "id")
    private User user;
    @ManyToOne
    @JoinColumn(name="utilityAccountNumber", nullable=false, updatable = false, insertable = false,referencedColumnName = "utilityAccountNumber")
    private UtilityAccount utilityAccount;
}
