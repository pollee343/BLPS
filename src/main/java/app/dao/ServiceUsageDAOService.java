package app.dao;

import app.model.entities.ServiceUsage;
import app.repositories.ServiceUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceUsageDAOService {

    private final ServiceUsageRepository serviceUsageRepository;

    public List<ServiceUsage> findByUserDataIdAndOperationTimeBetween(Long userDataId, LocalDateTime from, LocalDateTime to) {
        return serviceUsageRepository.findByUserDataIdAndOperationTimeBetween(userDataId, from, to);
    }

    public void save(ServiceUsage serviceUsage) {
        serviceUsageRepository.save(serviceUsage);
    }

}
