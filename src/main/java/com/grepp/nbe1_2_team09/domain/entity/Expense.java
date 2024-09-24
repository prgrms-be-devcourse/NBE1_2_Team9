package com.grepp.nbe1_2_team09.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "expense_tb")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expenseId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(nullable = false, length = 100)
    private String itemName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime expenseDate;

    //지불한 사람 기록
    @Column(nullable = false, length = 50)
    private String paidBy;

    @Lob
    @Column
    private byte[] receiptImage;

    @Builder
    public Expense(String itemName, BigDecimal amount, LocalDateTime expenseDate, String paidBy, Group group) {
        this.itemName = itemName;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.paidBy = paidBy;
        this.group = group;

    }

    public void updateExpenseDetails(String itemName, BigDecimal amount, LocalDateTime expenseDate) {
        this.itemName = itemName;
        this.amount = amount;
        this.expenseDate = expenseDate;
    }

    public void changePaidBy(String username){
        this.paidBy = username;
    }

    public void attachReceiptImage(byte[] receiptImage){
        this.receiptImage = receiptImage;
    }


}
