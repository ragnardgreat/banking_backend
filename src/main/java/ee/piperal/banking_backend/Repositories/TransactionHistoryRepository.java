package ee.piperal.banking_backend.Repositories;

import ee.piperal.banking_backend.Entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionHistoryRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t WHERE t.receiverId = :id")
    List<Transaction> findByReceiverId(@Param("id") Long id);

    @Query("SELECT t FROM Transaction t WHERE t.senderId = :id")
    List<Transaction> findBySenderId(@Param("id") Long id);
}
