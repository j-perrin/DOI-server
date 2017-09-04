/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cnes.doi.security;

import fr.cnes.doi.application.AdminApplication;
import fr.cnes.doi.application.DoiMdsApplication;
import fr.cnes.doi.db.ProjectSuffixDB;
import fr.cnes.doi.exception.DoiRuntimeException;
import fr.cnes.doi.plugin.PluginFactory;
import fr.cnes.doi.utils.UniqueProjectName;
import fr.cnes.doi.utils.Utils;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.restlet.Application;
import org.restlet.security.Group;
import org.restlet.security.MemoryRealm;
import org.restlet.security.Role;
import org.restlet.security.User;

/**
 *
 * @author Jean-Christophe Malapert (jean-christophe.malapert@cnes.fr)
 */
public class RoleAuthorizer implements Observer {

    public static String ROLE_ADMIN = "admin";
    public static String GROUP_USERS = "Users";
    public static String GROUP_ADMIN = "Administrator";

    /**
     * Logger.
     */
    public static final Logger LOGGER = Utils.getAppLogger();

    /**
     * Class to handle the instance
     *
     */
    private static class RoleAuthorizerHolder {

        /**
         * Unique Instance unique
         */
        private final static RoleAuthorizer INSTANCE = new RoleAuthorizer();
    }

    /**
     * Access to unique INSTANCE of role authorizer
     *
     * @return the configuration instance.
     */
    public static RoleAuthorizer getInstance() {
        return RoleAuthorizerHolder.INSTANCE;
    }

    private static final MemoryRealm REALM = new MemoryRealm();

    private RoleAuthorizer() {
        initUsersGroups();
    }

    private void initUsersGroups() {
        LOGGER.entering(this.getClass().getName(), "initUsersGroups");
        
        UserGroupMngtHelper userPlugin = PluginFactory.getUserManagement();
        
        // Add users
        userPlugin.addUsersToRealm(RoleAuthorizer.REALM.getUsers());
        
        // Add Groups                         
        Group users = new Group(GROUP_USERS, "Users");
        userPlugin.addUsersToUserGroup(users.getMemberUsers());
        RoleAuthorizer.REALM.getRootGroups().add(users);

        Group administrators = new Group(GROUP_ADMIN, "Administrators");
        userPlugin.addUsersToAdminGroup(administrators.getMemberUsers());
        RoleAuthorizer.REALM.getRootGroups().add(administrators);

        LOGGER.exiting(this.getClass().getName(), "initUsersGroups");
    }

    private void initForMds(Application app) {
        LOGGER.entering(this.getClass().getName(), "initForMds", new Object[]{REALM, app.getName()});

        initForAdmin(app);

        Map<String, Integer> projects = UniqueProjectName.getInstance().getProjects();
        for (String project : projects.keySet()) {
            Integer projectID = projects.get(project);
            for (Group group : RoleAuthorizer.REALM.getRootGroups()) {
                RoleAuthorizer.REALM.map(group, Role.get(app, String.valueOf(projectID)));
            }
        }

        app.getContext().setDefaultEnroler(RoleAuthorizer.REALM.getEnroler());
        app.getContext().setDefaultVerifier(RoleAuthorizer.REALM.getVerifier());

        LOGGER.exiting(this.getClass().getName(), "initForMds");
    }

    private Group findGroupByName(final String name) {
        List<Group> groups = RoleAuthorizer.REALM.getRootGroups();
        Group searchedGroup = null;
        for (Group group : groups) {
            LOGGER.fine(String.format("group name : %s", group.getName()));
            if (group.getName().equals(name)) {
                searchedGroup = group;
                LOGGER.fine("group found");
                break;
            }
        }
        if (searchedGroup == null) {
            LOGGER.fine("group not found");
            throw new RuntimeException(String.format("Please, create a group %s", name));
        }
        return searchedGroup;
    }

    private void initForAdmin(Application app) {
        LOGGER.entering(this.getClass().getName(), "initForAdmin", new Object[]{REALM, app.getName()});

        Group admin = findGroupByName(GROUP_ADMIN);

        RoleAuthorizer.REALM.map(admin, Role.get(app, ROLE_ADMIN));
        app.getContext().setDefaultEnroler(RoleAuthorizer.REALM.getEnroler());
        app.getContext().setDefaultVerifier(RoleAuthorizer.REALM.getVerifier());

        LOGGER.exiting(this.getClass().getName(), "initForAdmin");
    }

    /**
     * Init Realm for application.
     * @param app application
     * @return True when the realm is initialized otherwise False
     */
    public boolean createRealmFor(Application app) {
        LOGGER.entering(this.getClass().getName(), "createReamFor", app.getName());

        boolean isCreated;
        switch (app.getName()) {
            case DoiMdsApplication.NAME:
                initForMds(app);
                isCreated = true;
                LOGGER.fine("Init for MDS ... done");
                break;
            case AdminApplication.NAME:
                initForAdmin(app);
                isCreated = true;
                LOGGER.fine("Init for admin ... done");
                break;
            default:
                LOGGER.log(Level.WARNING, "No Realm is initialized for this application {0}", app.getName());
                isCreated = false;
                break;
        }

        LOGGER.exiting(this.getClass().getName(), "createReamFor", isCreated);

        return isCreated;
    }

    /**
     * Adds/removes the <i>users</i> group to the new role name related to the
     * application MDS
     *
     * @param o observable
     * @param obj message
     */
    @Override
    public void update(Observable o, Object obj) {
        LOGGER.entering(this.getClass().getName(), "update", new Object[]{o, obj});

        String[] message = (String[]) obj;

        if (o instanceof ProjectSuffixDB) {
            LOGGER.fine("Observable is a ProjectSuffixDB type");

            // Loads the admin group - admin group is defined by default
            Group adminGroup = findGroupByName(GROUP_ADMIN);

            // Loads the application MDS related to admin group
            Application mds = loadApplicationBy(adminGroup, DoiMdsApplication.NAME);

            // Loads the user group
            Group usersGroup = findGroupByName(GROUP_USERS);

            String operation = message[0];
            String roleName = message[1];
            switch (operation) {
                case ProjectSuffixDB.ADD_RECORD:
                    RoleAuthorizer.REALM.map(usersGroup, Role.get(mds, roleName));
                    LOGGER.log(Level.FINE, "Adds the {0} group to the new {1} related to the {2}", new Object[]{usersGroup.getName(), roleName, mds.getName()});
                    break;
                case ProjectSuffixDB.DELETE_RECORD:
                    RoleAuthorizer.REALM.unmap(usersGroup, mds, roleName);
                    LOGGER.log(Level.FINE, "Remove the {0} group to the {1} related to the {2}", new Object[]{usersGroup.getName(), roleName, mds.getName()});
                    break;
                default:
                    break;
            }

            LOGGER.log(Level.FINE, "Links user with role {0} for app {1}", new Object[]{roleName, mds.getName()});
        }

        LOGGER.exiting(this.getClass().getName(), "update");
    }

    /**
     * Loads the application related to a group with a name
     *
     * @param group group linked to an application
     * @param appName application name
     * @return the application
     */
    private Application loadApplicationBy(final Group group, final String appName) {
        LOGGER.entering(this.getClass().getName(), "loadApplicationBy", new Object[]{group.getName(), appName});

        Set<Role> roles = RoleAuthorizer.REALM.findRoles(group);
        Iterator<Role> roleIter = roles.iterator();
        Application searchedApp = null;
        while (roleIter.hasNext()) {
            Role role = roleIter.next();
            Application app = role.getApplication();
            if (app.getName().equals(appName)) {
                searchedApp = app;
            }
        }
        if (searchedApp == null) {
            throw new DoiRuntimeException("Application " + appName + " not found");
        }

        LOGGER.exiting(this.getClass().getName(), "loadApplicationBy", searchedApp);

        return searchedApp;
    }

}