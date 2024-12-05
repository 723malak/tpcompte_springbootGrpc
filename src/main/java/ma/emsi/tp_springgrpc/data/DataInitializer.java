package ma.emsi.tp_springgrpc.data;

import ma.emsi.tp_springgrpc.Repository.CompteRepository;
import ma.emsi.tp_springgrpc.entity.Compte;
import ma.emsi.tp_springgrpc.stubs.TypeCompte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DataInitializer {

    @Autowired
    private CompteRepository compteRepository;

    @PostConstruct
    public void init() {
        // Vérifier si les comptes existent déjà avant de les ajouter
        if (compteRepository.count() == 0) {
            compteRepository.save(new Compte("1", 1000.0f, "2024-12-01", TypeCompte.COURANT));
            compteRepository.save(new Compte("2", 500.0f, "2024-12-02", TypeCompte.EPARGNE));
            compteRepository.save(new Compte("3", 1500.0f, "2024-12-03", TypeCompte.COURANT));
        }
    }
}
