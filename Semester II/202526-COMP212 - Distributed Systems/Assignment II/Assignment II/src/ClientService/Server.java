package ClientService;


import Configuration.GlobalConfiguration;
import Interface.CastVote;
import Interface.Impl.CastVoteImpl;
import Log.Logging;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


import java.rmi.NoSuchObjectException;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    private static Registry registry;
    private static CastVote votingService;

    public static void run() throws Exception {
        votingService = new CastVoteImpl();
        // 创建注册表
        registry = LocateRegistry.createRegistry(GlobalConfiguration.PORT);
        // 绑定服务
        registry.rebind(GlobalConfiguration.RMI_SERVICE_NAME, votingService);

        Logging.info("SERVICE   | Server started on port {}.", GlobalConfiguration.PORT);
    }

    public static void terminate() {
        Logging.info("SERVICE   | Terminating server...");
        try {
            if (registry != null) {
                // 1. 从注册表中解绑
                registry.unbind(GlobalConfiguration.RMI_SERVICE_NAME);
                // 2. 强行释放注册表端口 (1099)
                UnicastRemoteObject.unexportObject(registry, true);
            }
            if (votingService != null) {
                // 3. 释放远程对象
                UnicastRemoteObject.unexportObject(votingService, true);
            }
            Logging.info("SERVICE   | Server terminated and port released.");
        } catch (NoSuchObjectException e) {
            Logging.error("SERVICE   | Server was not running.");
        } catch (Exception e) {
            Logging.error("SERVICE   | Error during termination: {}", e.getMessage());
        }
    }
}

