import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.mybatis.example.Blog;
import org.mybatis.example.BlogMapper;

import java.io.IOException;
import java.io.InputStream;

public class DBHandler {

    SqlSession getSqlSession(SqlSessionFactory sessionFactory) {
        return sessionFactory.openSession();
    }

    public SqlSessionFactory getSqlSessionFactory() throws IOException {
        String resource = "mybatis_config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        return sqlSessionFactory;
    }

    Blog getBlog(Integer blogId, SqlSessionFactory sqlSessionFactory) {
        SqlSession session = null;
        Blog blog;
        try {
            session = getSqlSession(sqlSessionFactory);
            BlogMapper mapper = session.getMapper(BlogMapper.class);
            blog = mapper.selectBlog(101);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return blog;
    }
}
