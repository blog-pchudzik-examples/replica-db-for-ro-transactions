package com.pchudzik.blog.example.readfromreplica.infrastructure;

import org.springframework.transaction.*;
import org.springframework.transaction.support.TransactionSynchronizationManager;

class ReplicaAwareTransactionManager implements PlatformTransactionManager {
    private final PlatformTransactionManager wrapped;

    ReplicaAwareTransactionManager(PlatformTransactionManager wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
        boolean isTxActive = TransactionSynchronizationManager.isActualTransactionActive();

        if (isTxActive && MasterReplicaRoutingDataSource.isCurrentlyReadonly() && !definition.isReadOnly()) {
            throw new CannotCreateTransactionException("Can not request RW transaction from initialized readonly transaction");
        }

        if (!isTxActive) {
            MasterReplicaRoutingDataSource.setReadonlyDataSource(definition.isReadOnly());
        }

        return wrapped.getTransaction(definition);
    }

    @Override
    public void commit(TransactionStatus status) throws TransactionException {
        wrapped.commit(status);
    }

    @Override
    public void rollback(TransactionStatus status) throws TransactionException {
        wrapped.rollback(status);
    }
}
