import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        DBHandler dbHandler = new DBHandler();
        SqlSessionFactory sessionFactory = dbHandler.getSqlSessionFactory();

        SqlSession sqlSession = dbHandler.getSqlSession(sessionFactory);


    }

}
