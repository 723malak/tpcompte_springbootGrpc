package ma.emsi.tp_springgrpc.controllers;

import io.grpc.stub.StreamObserver;
import ma.emsi.tp_springgrpc.stubs.*;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@GrpcService
public class CompteServiceimpl extends CompteServiceGrpc.CompteServiceImplBase {

    // Simuler une base de données en mémoire
    private final Map<String, Compte> compteDB = new ConcurrentHashMap<>();

    public CompteServiceimpl() {
        // Ajouter des comptes à la base de données en mémoire
        compteDB.put("1", Compte.newBuilder().setId("1").setSolde(1000.0f).setDateCreation("2024-12-01").setType(TypeCompte.COURANT).build());
        compteDB.put("2", Compte.newBuilder().setId("2").setSolde(500.0f).setDateCreation("2024-12-02").setType(TypeCompte.EPARGNE).build());
        compteDB.put("3", Compte.newBuilder().setId("3").setSolde(1500.0f).setDateCreation("2024-12-03").setType(TypeCompte.COURANT).build());
    }

    @Override
    public void allComptes(GetAllComptesRequest request, StreamObserver<GetAllComptesResponse> responseObserver) {
        GetAllComptesResponse.Builder responseBuilder = GetAllComptesResponse.newBuilder();
        responseBuilder.addAllComptes(compteDB.values());
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void compteById(GetCompteByIdRequest request, StreamObserver<GetCompteByIdResponse> responseObserver) {
        Compte compte = compteDB.get(request.getId());
        if (compte != null) {
            responseObserver.onNext(GetCompteByIdResponse.newBuilder().setCompte(compte).build());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(
                    io.grpc.Status.NOT_FOUND
                            .withDescription("Compte avec l'ID " + request.getId() + " non trouvé")
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void totalSolde(GetTotalSoldeRequest request,
                           StreamObserver<GetTotalSoldeResponse> responseObserver) {
        int count = compteDB.size();
        float sum = 0;
        for (Compte compte : compteDB.values()) {
            sum += compte.getSolde();
        }
        float average = count > 0 ? sum / count : 0;

        SoldeStats stats = SoldeStats.newBuilder()
                .setCount(count)
                .setSum(sum)
                .setAverage(average)
                .build();

        responseObserver.onNext(GetTotalSoldeResponse.newBuilder()
                .setStats(stats)
                .build());
        responseObserver.onCompleted();
    }
    @Override
    public void findByType(FindByTypeRequest request, StreamObserver<FindByTypeResponse> responseObserver) {
        // Filtrer les comptes par type
        TypeCompte requestedType = request.getType();
        List<Compte> filteredComptes = compteDB.values().stream()
                .filter(compte -> compte.getType() == requestedType)
                .toList();

        // Construire la réponse
        FindByTypeResponse response = FindByTypeResponse.newBuilder()
                .addAllComptes(filteredComptes)
                .build();

        // Envoyer la réponse
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
    @Override
    public void deleteCompteById(DeleteCompteByIdRequest request, StreamObserver<DeleteCompteByIdResponse> responseObserver) {
        String compteId = request.getId();

        // Vérifier si le compte existe
        if (compteDB.containsKey(compteId)) {
            // Supprimer le compte
            compteDB.remove(compteId);

            // Construire la réponse
            DeleteCompteByIdResponse response = DeleteCompteByIdResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Compte avec l'ID " + compteId + " a été supprimé avec succès.")
                    .build();

            // Envoyer la réponse
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            // Si le compte n'existe pas
            responseObserver.onError(
                    io.grpc.Status.NOT_FOUND
                            .withDescription("Compte avec l'ID " + compteId + " non trouvé.")
                            .asRuntimeException()
            );
        }
    }

}
