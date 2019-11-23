package com.pchudzik.blog.example.readfromreplica.infrastructure;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

class MasterReplicaRoutingDataSource extends AbstractRoutingDataSource {
    private static final ThreadLocal<Type> currentDataSource = new ThreadLocal<>();

    MasterReplicaRoutingDataSource(DataSource master, DataSource slave) {
        Map<Object, Object> dataSources = new HashMap<>();
        dataSources.put(Type.MASTER, master);
        dataSources.put(Type.REPLICA, slave);

        super.setTargetDataSources(dataSources);
        super.setDefaultTargetDataSource(master);
    }

    static void setReadonlyDataSource(boolean isReadonly) {
        currentDataSource.set(isReadonly ? Type.REPLICA : Type.MASTER);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return currentDataSource.get();
    }

    private enum Type {
        MASTER, REPLICA;
    }
}
