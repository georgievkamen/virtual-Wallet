package com.team9.virtualwallet.repositories.contracts;

import com.team9.virtualwallet.models.PaymentMethod;

public interface PaymentMethodRepository extends BaseRepository<PaymentMethod> {

    PaymentMethod getById(int id, String paymentMethod);
}
