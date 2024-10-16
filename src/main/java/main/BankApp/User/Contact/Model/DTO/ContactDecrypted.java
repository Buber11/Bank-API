package main.BankApp.User.Contact.Model.DTO;

import lombok.*;

@Data
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class ContactDecrypted {
    private Long contactId;
    private String name;
    private String numberAccount;
    private String numberOfUse;
    private String dateOfLastUse;
}
