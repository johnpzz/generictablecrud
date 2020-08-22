package dao;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.io.Serializable;
import java.util.List;

public abstract class AbstractDao<T extends Serializable> implements GenericDao<T> {

    //@PersistenceContext - I don't really know what this is
    EntityManager entityManager;
    private Class<T> type;

    AbstractDao(Class<T> type) {
        entityManager = Persistence.createEntityManagerFactory("validationPU").createEntityManager();
        this.type = type;
    }

    public T findOne(int id) {
        return entityManager.find( type, id );
    }

    public List<T> findAll() {
        return entityManager.createQuery( "from " + type.getName() ).getResultList();
    }

    public void create(T entity) {
        entityManager.getTransaction().begin();
        entityManager.persist( entity );
        entityManager.getTransaction().commit();
    }

    public void update(T entity) {
        //entityManager.merge( entity );
        entityManager.getTransaction().begin();
        entityManager.merge(entity);
        entityManager.getTransaction().commit();
    }

    public void delete(T entity) {
        entityManager.getTransaction().begin();
        entityManager.remove( entity );
        entityManager.getTransaction().commit();
    }

    public void deleteById( int entityId ){
        T entity = findOne( entityId );
        delete( entity );
    }
}
