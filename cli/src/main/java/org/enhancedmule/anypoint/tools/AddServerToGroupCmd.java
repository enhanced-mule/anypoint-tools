package org.enhancedmule.anypoint.tools;

import com.kloudtek.util.UserDisplayableException;
import org.enhancedmule.anypoint.tools.runtime.Server;
import org.enhancedmule.anypoint.tools.runtime.ServerGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "addservertogroup", description = "Add a server to a group", sortOptions = false)
public class AddServerToGroupCmd extends AbstractEnvironmentCmd {
    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help message")
    boolean usageHelpRequested;
    private static final Logger logger = LoggerFactory.getLogger(AddServerToGroupCmd.class);
    @Parameters(index = "0", description = "Server Group/Cluster name")
    private String groupName;
    @Parameters(index = "1", description = "Server name")
    private String serverName;

    @Override
    protected void execute(Environment environment) throws Exception {
        logger.info("Adding server " + serverName + " to server group/cluster " + groupName + " within env " + environment.getName() + " of org " + environment.getOrganization().getName());
        Server server;
        try {
            server = environment.findServerByName(serverName);
        } catch (NotFoundException e) {
            throw new UserDisplayableException("Unable to find server " + serverName + " in org '" + environment.getOrganization().getName() + "', env '" + environment.getName() + "'");
        }
        try {
            Server group = environment.findServerByName(groupName);
            if (group instanceof ServerGroup) {
                ((ServerGroup) group).addServer(server);
            } else {
                throw new UserDisplayableException(groupName + " is not a group or cluster");
            }
        } catch (NotFoundException e) {
            System.out.println("Server group " + groupName + " does not not exist, creating");
            environment.createServerGroup(groupName, server.getId());
        }
        logger.info("Server " + serverName + " added to group " + groupName);
    }
}
