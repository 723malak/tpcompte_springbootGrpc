package ma.emsi.tp_springgrpc.controllers;

import io.grpc.stub.StreamObserver;
import ma.emsi.tp_springgrpc.stubs.*;
import ma.emsi.tp_springgrpc.entity.Compte;
import ma.emsi.tp_springgrpc.Repository.CompteRepository;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@GrpcService
public class CompteServiceImpl extends CompteServiceGrpc.CompteServiceImplBase {

    private final CompteRepository compteRepository;

    // Constructor to inject the repository
    public CompteServiceImpl(CompteRepository compteRepository) {
        this.compteRepository = compteRepository;
    }

    // Mapper method to convert entity to gRPC message
    private ma.emsi.tp_springgrpc.stubs.Compte mapToGrpcCompte(ma.emsi.tp_springgrpc.entity.Compte compteEntity) {
        return ma.emsi.tp_springgrpc.stubs.Compte.newBuilder()
                .setId(compteEntity.getId())
                .setSolde(compteEntity.getSolde())
                .setType(compteEntity.getType()) // Assuming this maps correctly
                .build();
    }

    @Override
    public void allComptes(GetAllComptesRequest request, StreamObserver<GetAllComptesResponse> responseObserver) {
        // Get all accounts from the database
        List<ma.emsi.tp_springgrpc.stubs.Compte> grpcComptes = compteRepository.findAll().stream()
                .map(this::mapToGrpcCompte)  // Convert each entity to gRPC
                .collect(Collectors.toList());

        // Create and send the response
        GetAllComptesResponse response = GetAllComptesResponse.newBuilder()
                .addAllComptes(grpcComptes)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void compteById(GetCompteByIdRequest request, StreamObserver<GetCompteByIdResponse> responseObserver) {
        // Retrieve the account by ID
        ma.emsi.tp_springgrpc.entity.Compte compteEntity = compteRepository.findById(request.getId()).orElse(null);

        if (compteEntity != null) {
            // Convert the entity to gRPC and send the response
            ma.emsi.tp_springgrpc.stubs.Compte grpcCompte = mapToGrpcCompte(compteEntity);
            responseObserver.onNext(GetCompteByIdResponse.newBuilder().setCompte(grpcCompte).build());
            responseObserver.onCompleted();
        } else {
            // Send error if account is not found
            responseObserver.onError(
                    io.grpc.Status.NOT_FOUND
                            .withDescription("Compte avec l'ID " + request.getId() + " non trouvé.")
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void totalSolde(GetTotalSoldeRequest request, StreamObserver<GetTotalSoldeResponse> responseObserver) {
        // Calculate the sum, count, and average of all accounts' balances
        List<ma.emsi.tp_springgrpc.entity.Compte> comptes = compteRepository.findAll();
        int count = comptes.size();
        float sum = (float) comptes.stream().mapToDouble(Compte::getSolde).sum();
        float average = count > 0 ? sum / count : 0;

        // Build the stats
        SoldeStats stats = SoldeStats.newBuilder()
                .setCount(count)
                .setSum(sum)
                .setAverage(average)
                .build();

        // Send the response with the stats
        responseObserver.onNext(GetTotalSoldeResponse.newBuilder()
                .setStats(stats)
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void findByType(FindByTypeRequest request, StreamObserver<FindByTypeResponse> responseObserver) {
        // Filter accounts by type
        List<ma.emsi.tp_springgrpc.stubs.Compte> filteredComptes = compteRepository.findAll().stream()
                .filter(compte -> compte.getType() == request.getType())
                .map(this::mapToGrpcCompte) // Convert each to gRPC
                .collect(Collectors.toList());

        // Create the response with filtered accounts
        FindByTypeResponse response = FindByTypeResponse.newBuilder()
                .addAllComptes(filteredComptes)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteCompteById(DeleteCompteByIdRequest request, StreamObserver<DeleteCompteByIdResponse> responseObserver) {
        // Check if the account exists
        if (compteRepository.existsById(request.getId())) {
            // Delete the account
            compteRepository.deleteById(request.getId());

            // Build the response
            DeleteCompteByIdResponse response = DeleteCompteByIdResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Compte avec l'ID " + request.getId() + " a été supprimé avec succès.")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            // Send error if account not found
            responseObserver.onError(
                    io.grpc.Status.NOT_FOUND
                            .withDescription("Compte avec l'ID " + request.getId() + " non trouvé.")
                            .asRuntimeException()
            );
        }
    }
}
