package us.vanderlugt.sample.audit.common;

import org.hibernate.envers.RevisionListener;

public class AuditListener implements RevisionListener {
    @Override
    public void newRevision(Object revisionEntity) {

    }
}
