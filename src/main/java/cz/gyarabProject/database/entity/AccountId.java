package cz.gyarabProject.database.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "account_ids")
public class AccountId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true, name = "bank_account_id")
    private String bankAccountId;

    protected AccountId() {}

    public AccountId(Long userId, String bankAccountId) {
        this.userId = userId;
        this.bankAccountId = bankAccountId;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getBankAccountId() { return bankAccountId; }
}
