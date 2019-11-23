package com.pchudzik.blog.example.readfromreplica.infrastructure;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

class ReplicaAwareTransactionManager implements PlatformTransactionManager {
    private final PlatformTransactionManager wrapped;

    ReplicaAwareTransactionManager(PlatformTransactionManager wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
        MasterReplicaRoutingDataSource.setReadonlyDataSource(definition.isReadOnly());
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
