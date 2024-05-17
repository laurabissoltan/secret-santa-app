package kz.hackathon.secretsantaapp.service;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import kz.hackathon.secretsantaapp.model.invitation.Invitation;
import kz.hackathon.secretsantaapp.model.invitation.InvitationStatus;
import kz.hackathon.secretsantaapp.model.game.Game;
import kz.hackathon.secretsantaapp.repository.GameRepository;
import kz.hackathon.secretsantaapp.repository.InvitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvitationService {

    @Autowired
    private InvitationRepository invitationRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private EmailService emailService;
    public void sendInvitations(UUID gameId, List<String> emails) throws MessagingException {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found"));
        String groupInvitationLink = generateShareableLink(gameId);
        String groupInvitationLinkIOS = generateShareableLinkIOS(gameId);

        String messageTemplate = "<p>Вас приглашают участвовать в игре Secret Santa!</p>" +
                "<p>Пожалуйста, перейдите по одной из следующих ссылок для участия:</p>" +
                "<ul>" +
                "<li>Для общего доступа: <a href=\"%s\">Принять приглашение</a> ( или скопируйте и вставьте ссылку в ваш браузер: %s )</li>" +
                "<li>Для пользователей iOS: <a href=\"%s\">Открыть в приложении</a> ( или скопируйте и вставьте ссылку в ваш браузер: %s )</li>" +
                "</ul>";

        for (String email : emails) {
            String message = String.format(messageTemplate,
                    groupInvitationLink, groupInvitationLink,
                    groupInvitationLinkIOS, groupInvitationLinkIOS);
            // String message = String.format(messageTemplate, groupInvitationLink, groupInvitationLinkIOS);
            emailService.sendHtmlEmail(email, "Вас приглашают участвовать в игре Secret Santa!", message);
        }
    }


    public String generateShareableLink(UUID gameId) {
        Optional<Invitation> existingGroupInvitation = invitationRepository.findByGameIdAndGroupInvitationTrue(gameId);

        String invitationCode;
        if (existingGroupInvitation.isPresent()) {
            invitationCode = existingGroupInvitation.get().getInvitationCode();
        } else {
            Game game = gameRepository.findById(gameId)
                    .orElseThrow(() -> new RuntimeException("Game not found"));
            Invitation invitation = new Invitation();
            invitation.setGame(game);
            invitation.setInvitationCode(UUID.randomUUID().toString());
            invitation.setStatus(InvitationStatus.PENDING);
            invitation.setGroupInvitation(true);
            invitationRepository.save(invitation);

            invitationCode = invitation.getInvitationCode();
        }
        https://secret-santa-app.azurewebsites.net/
     //   return "http://51.107.14.25:3000/invitations/accept/" + invitationCode;
        return "http://158.160.21.73:3000/invitations/accept/" + invitationCode;
    }

    public String generateShareableLinkIOS(UUID gameId) {
        Optional<Invitation> existingGroupInvitation = invitationRepository.findByGameIdAndGroupInvitationTrue(gameId);

        String invitationCode;
        if (existingGroupInvitation.isPresent()) {
            invitationCode = existingGroupInvitation.get().getInvitationCode();
        } else {
            Game game = gameRepository.findById(gameId)
                    .orElseThrow(() -> new RuntimeException("Game not found"));
            Invitation invitation = new Invitation();
            invitation.setGame(game);
            invitation.setInvitationCode(UUID.randomUUID().toString());
            invitation.setStatus(InvitationStatus.PENDING);
            invitation.setGroupInvitation(true);
            invitationRepository.save(invitation);

            invitationCode = invitation.getInvitationCode();
        }

        return "deeplink://" + invitationCode;
    }


}