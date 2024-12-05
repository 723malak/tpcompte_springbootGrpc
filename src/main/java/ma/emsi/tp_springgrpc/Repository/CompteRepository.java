package ma.emsi.tp_springgrpc.Repository;

import ma.emsi.tp_springgrpc.entity.Compte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompteRepository extends JpaRepository<Compte, String> {
}

