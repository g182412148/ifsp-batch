import com.alibaba.druid.pool.DruidDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CyclicBarrier;

public class TestStatementOutTime {

    private static DruidDataSource dataSource;

    static {
        if(dataSource == null){
            dataSource = new DruidDataSource();
            dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
            dataSource.setUrl("jdbc:oracle:thin:@localhost:1521/orcl");
            dataSource.setUsername("BATCHDATA");
            dataSource.setPassword("BATCHDATA");
            dataSource.setInitialSize(10);
            dataSource.setMinIdle(10);
            dataSource.setMaxActive(200);
            dataSource.setMaxWait(10000);
            dataSource.setTimeBetweenEvictionRunsMillis(60000);
            dataSource.setMinEvictableIdleTimeMillis(300000);
            dataSource.setValidationQuery("SELECT 'x' FROM DUAL");
//            dataSource.setConnectionInitSqls("SELECT 'x' FROM DUAL");
            dataSource.setTestWhileIdle(true);
            dataSource.setTestOnBorrow(false);
            dataSource.setTestOnReturn(false);
            dataSource.setPoolPreparedStatements(true);
            dataSource.setMaxPoolPreparedStatementPerConnectionSize(1);
            try {
                dataSource.setFilters("stat,wall");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            dataSource.setConnectionProperties("druid.stat.slowSqlMillis=3000");
            dataSource.setUseGlobalDataSourceStat(true);
        }
    }

    public static Connection getConnect() throws Exception{
        return dataSource.getConnection();
    }

    public static void insert() throws Exception {
        String sql = "INSERT INTO APP_LIST VALUES (?, ?, ? ,?)";
        PreparedStatement ps = getConnect().prepareStatement(sql);
        for( int i = 0; i< 150000; i++){
            ps.setString(1, i+"");
            ps.setString(2, i+"");
            ps.setString(3, i+"");
            ps.setString(4, i+"");
            ps.execute();
            System.out.println("插入:" + i);
        }
        ps.close();
    }

    public static void update(String i) throws Exception {
        Connection connect = getConnect();
        PreparedStatement ps = null;
        try{
            while (true){
                String sql = "UPDATE APP_LIST set SYS_NAME = 's' where SYS_ID = ?";
                ps = connect.prepareStatement(sql);
                ps.setString(1, i);
                ps.executeUpdate();
                System.out.println("更新:" + i) ;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            ps.close();
            connect.close();
        }

    }

    public static void main(String[] args) throws Exception {
        //更新
        int count = 200;
        CyclicBarrier cb = new CyclicBarrier(count);
        for(int i = 0; i<count; i++){
            new Thread(new UpdateThread(i + "", cb)).start();
            System.out.println("创建子线程:" + i);
        }
        //批量插入
        insert();
    }

    static class UpdateThread implements Runnable{
        private String i;
        private CyclicBarrier cb;

        public UpdateThread(String i,  CyclicBarrier cb) {
            this.i = i;
            this.cb = cb;
        }

        @Override
        public void run() {
            try {
                cb.await();
                update(i);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
        }
    }
}
