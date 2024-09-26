package ru.korundm.dao;

import ru.korundm.entity.Snapshot;

public interface SnapshotService extends CommonService<Snapshot> {

    void deleteAll();
}