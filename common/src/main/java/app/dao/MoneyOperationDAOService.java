package app.dao;

import app.model.enams.OperationType;
import app.model.entities.Bank;
import app.model.entities.MoneyOperation;
import app.repositories.MoneyOperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MoneyOperationDAOService {

    private final MoneyOperationRepository moneyOperationRepository;

    public List<MoneyOperation> findByUserDataId(Long userDataId) {
        return moneyOperationRepository.findByUserDataId(userDataId);
    }

    public List<MoneyOperation> findByUserDataIdAndOperationTimeBetween(Long userDataId,
                                                                        LocalDateTime from,
                                                                        LocalDateTime to) {
        return moneyOperationRepository.findByUserDataIdAndOperationTimeBetween(userDataId, from, to);
    }

    public List<MoneyOperation> findByUserDataIdAndOperationTimeBetweenAndType(Long userDataId,
                                                                               LocalDateTime from,
                                                                               LocalDateTime to,
                                                                               OperationType type) {
        return moneyOperationRepository.findByUserDataIdAndOperationTimeBetweenAndType(userDataId, from, to, type);
    }

    public List<MoneyOperation> findByUserDataIdAndOperationTimeBetweenAndNameLike(Long userDataId,
                                                                                   LocalDateTime from,
                                                                                   LocalDateTime to,
                                                                                   String name) {
        return moneyOperationRepository.findByUserDataIdAndOperationTimeBetweenAndNameLike(userDataId, from, to, name);
    }

    public List<MoneyOperation> findByUserDataIdAndOperationTimeBefore(Long userDataId, LocalDateTime from) {
        return moneyOperationRepository.findByUserDataIdAndOperationTimeBefore(userDataId, from);
    }

    public void save(MoneyOperation moneyOperation) {
        moneyOperationRepository.save(moneyOperation);
    }
}
