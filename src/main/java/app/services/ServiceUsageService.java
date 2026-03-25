package app.services;

import app.model.enams.UsageDirection;
import app.model.enams.UsageType;
import app.model.entities.ServiceUsage;
import app.model.entities.UserData;
import app.repositories.ServiceUsageRepository;
import app.repositories.UserDataRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceUsageService {

    @Autowired
    private ServiceUsageRepository serviceUsageRepository;
    @Autowired
    private UserDataRepository userDataRepository;

    @Transactional
    public void createServiceUsage(ServiceUsage serviceUsage) {
        UserData userData = userDataRepository
                .findByAccountNumber(serviceUsage.getUserData().getAccountNumber())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с заданным лицевым счетом не найден"));

        if (serviceUsage.getDirection().equals(UsageDirection.OUTGOING)){
            if (serviceUsage.getOperationType().equals(UsageType.CALL)){
                if (userData.getRemainingSeconds() - serviceUsage.getUnitsUsed() < 0){
                    throw new IllegalArgumentException("У пользователя недостаточно минут для совершения звонка");
                }
                userData.setRemainingSeconds(userData.getRemainingSeconds() - serviceUsage.getUnitsUsed());
                userDataRepository.save(userData);
            } else if (serviceUsage.getOperationType().equals(UsageType.SMS)){
                if (userData.getRemainingSms() - serviceUsage.getUnitsUsed() < 0){
                    throw new IllegalArgumentException("У пользователя недостаточно слов для отправки сообщения");
                }
                userData.setRemainingSms(userData.getRemainingSms() - serviceUsage.getUnitsUsed());
                userDataRepository.save(userData);
            } else if (serviceUsage.getOperationType().equals(UsageType.INTERNET)){
                if (userData.getRemainingBytes() - serviceUsage.getUnitsUsed() < 0){
                    throw new IllegalArgumentException("У пользователя недостаточно интернета");
                }
                userData.setRemainingBytes(userData.getRemainingBytes() - serviceUsage.getUnitsUsed());
                userDataRepository.save(userData);
            }
        }
        serviceUsage.setUserData(userData);
        serviceUsageRepository.save(serviceUsage);
    }
}
