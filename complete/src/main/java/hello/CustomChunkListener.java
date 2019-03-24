package hello;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Evjen on 23.03.2019.
 */
@Component
public class CustomChunkListener extends ChunkExceptionsBridge implements ChunkListener {

    private final DataSource dataSource;

    public CustomChunkListener(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void beforeChunk(ChunkContext context) {

    }

    @Override
    @Transactional
    public void afterChunk(ChunkContext context) {

        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);

        String sql = "insert into err(text) values(?)";

        List<String> strings = getAllExceptions();

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setString(1, strings.get(i));
            }

            @Override
            public int getBatchSize() {
                return strings.size();
            }
        });
    }

//    public void insertBatch(final List<Customer> customers){
//
//        String sql = "INSERT INTO CUSTOMER " +
//                "(CUST_ID, NAME, AGE) VALUES (?, ?, ?)";
//
//        getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
//
//            @Override
//            public void setValues(PreparedStatement ps, int i) throws SQLException {
//                Customer customer = customers.get(i);
//                ps.setLong(1, customer.getCustId());
//                ps.setString(2, customer.getName());
//                ps.setInt(3, customer.getAge() );
//            }
//
//            @Override
//            public int getBatchSize() {
//                return customers.size();
//            }
//        });
//    }

    @Override
    public void afterChunkError(ChunkContext context) {

    }
}
