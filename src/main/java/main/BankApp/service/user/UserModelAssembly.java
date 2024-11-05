package main.BankApp.service.user;

import main.BankApp.controller.AuthController;
import main.BankApp.controller.UserController;
import main.BankApp.dto.UserModel;
import main.BankApp.expection.RSAException;
import main.BankApp.model.user.StatusAccount;
import main.BankApp.model.user.UserAccount;
import main.BankApp.model.user.UserPersonalData;
import main.BankApp.repository.UserRepository;
import main.BankApp.service.rsa.RSAService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembly extends RepresentationModelAssemblerSupport<UserAccount, UserModel> {

    private final RSAService rsaService;

    public UserModelAssembly(RSAService theRsaService) {
        super(UserController.class, UserModel.class);
        rsaService = theRsaService;
    }

    @Override
    public UserModel toModel(UserAccount entity) {
        UserPersonalData userPersonalData = entity.getUserPersonalData();
        UserModel userModel = null;
        try {
            userModel = UserModel.builder()
                    .userId(entity.getUserId())
                    .username(entity.getUsername())
                    .email(rsaService.decrypt(entity.getEmail()))
                    .status(entity.getStatus())
                    .lastLogin(entity.getLastLogin())
                    .twoFactorEnabled(entity.isTwoFactorEnabled())
                    .consentToCommunication(entity.isConsentToCommunication())
                    .firstName(rsaService.decrypt(userPersonalData.getFirstName()))
                    .lastName(rsaService.decrypt(userPersonalData.getLastName()))
                    .countryOfOrigin(rsaService.decrypt(userPersonalData.getCountryOfOrigin()))
                    .phoneNumber(rsaService.decrypt(userPersonalData.getPhoneNumber()))
                    .pesel(rsaService.decrypt(userPersonalData.getPesel()))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        userModel.add(linkTo(methodOn(UserController.class).getUsers(entity.getStatus()))
                .withSelfRel());

        userModel.add(linkTo(methodOn(UserController.class).getUsers(StatusAccount.ACTIVE))
                .withRel("active-user"));
        userModel.add(linkTo(methodOn(UserController.class).getUsers(StatusAccount.LOCKED))
                .withRel("locked-user"));
        userModel.add(linkTo(methodOn(UserController.class).getUsers(StatusAccount.CLOSED))
                .withRel("closed-user"));
        userModel.add(linkTo(methodOn(UserController.class).getUsers(StatusAccount.SUSPENDED))
                .withRel("suspend-user"));

        return userModel;
    }

    public UserModel toModelAuthenticate(UserAccount entity) {
        UserPersonalData userPersonalData = entity.getUserPersonalData();
        UserModel userModel = null;
        try {
            userModel = UserModel.builder()
                    .userId(entity.getUserId())
                    .username(entity.getUsername())
                    .email(rsaService.decrypt(entity.getEmail()))
                    .status(entity.getStatus())
                    .lastLogin(entity.getLastLogin())
                    .twoFactorEnabled(entity.isTwoFactorEnabled())
                    .consentToCommunication(entity.isConsentToCommunication())
                    .role(entity.getRole())
                    .firstName(rsaService.decrypt(userPersonalData.getFirstName()))
                    .lastName(rsaService.decrypt(userPersonalData.getLastName()))
                    .countryOfOrigin(rsaService.decrypt(userPersonalData.getCountryOfOrigin()))
                    .phoneNumber(rsaService.decrypt(userPersonalData.getPhoneNumber()))
                    .pesel(rsaService.decrypt(userPersonalData.getPesel()))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        userModel.add(linkTo(methodOn(AuthController.class).login(null,null,null))
                .withSelfRel());
        userModel.add(linkTo(methodOn(AuthController.class).logout(null))
                .withRel("logout"));
        userModel.add(linkTo(methodOn(AuthController.class).deactivate(null))
                .withRel("deactivate"));
        userModel.add(linkTo(methodOn(AuthController.class).refreshToken(null,null))
                .withRel("refresh-token"));

        return userModel;
    }

}