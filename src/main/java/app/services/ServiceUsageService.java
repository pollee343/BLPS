package app.services;

import app.dao.ServiceUsageDAOService;
import app.dao.UserDataDAOService;
import app.model.enams.UsageDirection;
import app.model.enams.UsageType;
import app.model.entities.ServiceUsage;
import app.model.entities.UserData;
import app.services.interfases.ServiceUsageServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceUsageService implements ServiceUsageServiceInterface {

    private final ServiceUsageDAOService serviceUsageDAOService;
    private final UserDataDAOService userDataDAOService;

    @Override
    @Transactional
    public void createServiceUsage(ServiceUsage serviceUsage) {
        UserData userData = userDataDAOService
                .findByAccountNumber(serviceUsage.getUserData().getAccountNumber())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с заданным лицевым счетом не найден"));

        if (serviceUsage.getDirection().equals(UsageDirection.OUTGOING)){
            if (serviceUsage.getOperationType().equals(UsageType.CALL)){
                if (userData.getRemainingSeconds() - serviceUsage.getUnitsUsed() < 0){
                    throw new IllegalArgumentException("У пользователя недостаточно минут для совершения звонка");
                }
                userData.setRemainingSeconds(userData.getRemainingSeconds() - serviceUsage.getUnitsUsed());
                userDataDAOService.save(userData);
            } else if (serviceUsage.getOperationType().equals(UsageType.SMS)){
                if (userData.getRemainingSms() - serviceUsage.getUnitsUsed() < 0){
                    throw new IllegalArgumentException("У пользователя недостаточно слов для отправки сообщения");
                }
                userData.setRemainingSms(userData.getRemainingSms() - serviceUsage.getUnitsUsed());
                userDataDAOService.save(userData);
            } else if (serviceUsage.getOperationType().equals(UsageType.INTERNET)){
                if (userData.getRemainingBytes() - serviceUsage.getUnitsUsed() < 0){
                    throw new IllegalArgumentException("У пользователя недостаточно интернета");
                }
                userData.setRemainingBytes(userData.getRemainingBytes() - serviceUsage.getUnitsUsed());
                userDataDAOService.save(userData);
            }
        }
        serviceUsage.setUserData(userData);
        serviceUsageDAOService.save(serviceUsage);
    }
}
