package org.kwater.persistence.mybatis.aio;

import org.apache.ibatis.session.SqlSession;
import org.kwater.domain.aio.AiProcessInitDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

//@Repository
//public class DashboardRepository {
//    @Autowired
//    private DashboardMapper aiReceivingInitMapper;
//    public AiProcessInitDTO getAiReceivingInit(String item) {
//        System.err.println("getAiReceivingInit starts");
//        return aiReceivingInitMapper.getAiReceivingInit(item);
//    }
//}
@Component
public class DashboardRepository {
    private final SqlSession sqlSession;
    public DashboardRepository(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }
    public AiProcessInitDTO getAiReceivingInit(String item) {
        System.err.println("getAiReceivingInit starts");
        return this.sqlSession.selectOne("getAiReceivingInit",item);
    }
}
