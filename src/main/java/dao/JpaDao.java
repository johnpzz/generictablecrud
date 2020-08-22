package dao;

public class JpaDao<T> extends AbstractDao {

    protected Class<T> type;

    public JpaDao(Class<T> type) {
        super(type);
        this.type = type;
    }

    public Class<T> getType() {
        return type;
    }

}
