package com.nhnent.eat.sampleCustomAPI;

import co.paralleluniverse.fibers.SuspendExecution;
import com.nhnent.eat.customScenario.BaseCustomAPI;
import com.nhnent.eat.entity.ScenarioExecutionResult;
import com.nhnent.eat.entity.ScenarioUnit;
import com.nhnent.eat.entity.ScenarioUnitType;
import javafx.util.Pair;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class customAPI extends BaseCustomAPI {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Boolean executeExtraFunction(ScenarioExecutionResult scenarioResult, ScenarioUnit scenario)
            throws SuspendExecution, InterruptedException {

        this.scenarioResult = scenarioResult;

        if (scenario.extraFunctionName.equals("getUserNickname")) {
            String userNickname = getUserNickname();
            logger.info("User Nickname is {}", userNickname);
            logger.info("Set runtimeVar (Key:{}, Value:{})", scenario.returnVariableName, userNickname);
            runtimeVar.put(scenario.returnVariableName, userNickname);
        }

        if (scenario.extraFunctionName.equals("illegalCustomApi")) {
            return Boolean.FALSE;
        }

        if(scenario.extraFunctionName.equals("RequestGameDecision")) {
            RequestGameDecision();
        }

        return Boolean.TRUE;
    }


    public String getUserNickname() throws SuspendExecution {
        logger.info("Custom function called: <getUserNickname()>");

        try {
            Pair<String, byte[]> recvPck;
            String userNickname;

            while (true) {
                recvPck = recvBodyPacket();

                if (recvPck.getKey().equals("ResponseLogin")) {
                    String realJson = this.decodePacket(recvPck.getKey(), recvPck.getValue());

                    userNickname = Util.extractUserNickName(realJson);

                    break;
                }
            }

            logger.info("<Packet - ResponseLogin> UserNickname : {}", userNickname);
            return userNickname;
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }

        return null;
    }

    public void RequestGameDecision() throws SuspendExecution {

        ScenarioUnit scenarioUnit = new ScenarioUnit();

        scenarioUnit.type = ScenarioUnitType.Send;
        scenarioUnit.packageName = "tutorial";
        scenarioUnit.name = "RequestGameDecision";
//        scenarioUnit.json = "{\n" +
//                "        \"decision\" : 2\n" +
//                "    }";
        scenarioUnit.json = "{ \"decision\" : 2 }";
//        {
//            "decision" : 2
//        }

        sendPacketToServer(scenarioUnit);
    }
}
