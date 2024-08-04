package fr.theskyblockman.skylayer.shared_team;

import com.google.protobuf.ByteString;
import fr.theskyblockman.skylayer.shared_team.local.LocalSharedTeam;
import fr.theskyblockman.skylayer.shared_team.local.LocalTeamCollection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public interface SharedTeamManager {
    /**
     * A default implementation of this interface that saves the teams on local disk.
     * This implementation works for single-server setups.
     * You must implement {@code SharedTeamManager} for more complex setups.
     * The data is stored as a Protocol Buffer file.
     */
    class LocalSharedTeamManager implements SharedTeamManager {
        private final Path dataFile;

        private LocalTeamCollection teamData = LocalTeamCollection.getDefaultInstance();

        private static final Logger logger = Logger.getLogger(LocalSharedTeamManager.class.getName());

        /**
         * Creates a new instance of {@code LocalSharedTeamManager} with the given data file.
         *
         * @param dataFile The file to store the team data in. If null, the data will not be saved.
         */
        LocalSharedTeamManager(@Nullable Path dataFile) {
            this.dataFile = dataFile;

            if (dataFile == null) {
                return;
            }

            try {
                //noinspection ResultOfMethodCallIgnored
                dataFile.getParent().toFile().mkdirs();
                //noinspection ResultOfMethodCallIgnored
                dataFile.toFile().createNewFile();
            } catch (IOException e) {
                logger.severe("Could not create the team data file.");
            }

            loadTeams();
        }

        private void loadTeams() {
            if (dataFile == null || !dataFile.toFile().exists()) {
                teamData = LocalTeamCollection.getDefaultInstance();
                return;
            }

            try {
                teamData = LocalTeamCollection.parseFrom(Files.newInputStream(dataFile));
            } catch (IOException e) {
                logger.severe("Could not load the team data from file.");
            }
        }

        private void saveTeams() {
            if (dataFile == null) {
                return;
            }

            try {
                teamData.writeTo(Files.newOutputStream(dataFile));
            } catch (IOException e) {
                logger.severe("Could not save the team data to file.");
            }
        }

        private Optional<LocalSharedTeam> getTeam(String teamName) {
            for (LocalSharedTeam team : teamData.getTeamsList()) {
                if (team.getName().equals(teamName)) {
                    return Optional.of(team);
                }
            }

            return Optional.empty();
        }

        @Override
        public String[] getTeamNamesForPlayer(String playerId) {
            List<String> teamNames = new ArrayList<>();
            for (LocalSharedTeam team : teamData.getTeamsList()) {
                if (team.getMembersMap().containsKey(playerId)) {
                    teamNames.add(team.getName());
                }
            }

            return teamNames.toArray(new String[0]);
        }

        @Override
        public String[] listTeams() {
            List<String> teamNames = new ArrayList<>();
            for (LocalSharedTeam team : teamData.getTeamsList()) {
                teamNames.add(team.getName());
            }
            return teamNames.toArray(new String[0]);
        }

        @Override
        public byte[] accessPlayerDataForTeam(String playerId, String teamName) throws UnknownError {
            LocalSharedTeam curTeam = getTeam(teamName).orElseThrow(() -> new UnknownError("The team does not exist."));

            return curTeam
                    .getMembersMap()
                    .getOrDefault(playerId, curTeam.getDefaultData())
                    .toByteArray();
        }

        @Override
        public byte @Nullable [] removePlayerFromTeam(String playerId, String teamName, boolean deletedAttachedData) throws UnknownError {
            LocalSharedTeam curTeam = getTeam(teamName).orElseThrow(() -> new UnknownError("The team does not exist."));
            LocalSharedTeam.Builder builder = curTeam.toBuilder();

            ByteString data = builder
                    .getMembersMap()
                    .remove(playerId);

            teamData = teamData.toBuilder().setTeams(teamData.getTeamsList().indexOf(curTeam), builder).build();

            saveTeams();

            if (deletedAttachedData) {
                return data.toByteArray();
            } else {
                return null;
            }
        }

        @Override
        public void bulkRemovePlayersFromTeam(String[] playerIds, String teamName) throws UnknownError {
            LocalSharedTeam curTeam = getTeam(teamName).orElseThrow(() -> new UnknownError("The team does not exist."));
            LocalSharedTeam.Builder builder = curTeam.toBuilder();

            for (String playerId : playerIds) {
                builder.removeMembers(playerId);
            }

            teamData = teamData.toBuilder().setTeams(teamData.getTeamsList().indexOf(curTeam), builder).build();

            saveTeams();
        }

        @Override
        public void addPlayerToTeam(String playerId, String teamName, byte[] data) throws UnknownError {
            LocalSharedTeam curTeam = getTeam(teamName).orElseThrow(() -> new UnknownError("The team does not exist."));
            LocalSharedTeam.Builder newTeam = curTeam.toBuilder();

            newTeam
                    .putMembers(playerId, data == null ? ByteString.EMPTY : ByteString.copyFrom(data));

            teamData = teamData.toBuilder()
                    .setTeams(teamData.getTeamsList().indexOf(curTeam), newTeam)
                    .build();

            saveTeams();
        }

        @Override
        public void bulkAddPlayersToTeamWithData(Map<String, byte[]> playerData, String teamName) throws UnknownError {
            LocalSharedTeam curTeam = getTeam(teamName).orElseThrow(() -> new UnknownError("The team does not exist."));

            LocalSharedTeam.Builder newTeam = curTeam.toBuilder();

            for (Map.Entry<String, byte[]> entry : playerData.entrySet()) {
                newTeam.putMembers(entry.getKey(), ByteString.copyFrom(entry.getValue()));
            }

            teamData = teamData.toBuilder()
                    .setTeams(teamData.getTeamsList().indexOf(curTeam), newTeam)
                    .build();

            saveTeams();
        }

        @Override
        public void bulkAddPlayersToTeamWithoutData(String[] playerData, String teamName) throws UnknownError {
            LocalSharedTeam curTeam = getTeam(teamName).orElseThrow(() -> new UnknownError("The team does not exist."));

            LocalSharedTeam.Builder newTeam = curTeam.toBuilder();

            for (String playerId : playerData) {
                newTeam
                        .putMembers(playerId, ByteString.EMPTY);
            }

            teamData = teamData.toBuilder()
                    .setTeams(teamData.getTeamsList().indexOf(curTeam), newTeam)
                    .build();

            saveTeams();
        }

        @Override
        public void includeTeamMembersInAnotherTeam(String targetTeamName, String teamNameToInclude) {
            LocalSharedTeam targetTeam = getTeam(targetTeamName).orElseThrow(() -> new UnknownError("The target team does not exist."));
            LocalSharedTeam teamToInclude = getTeam(teamNameToInclude).orElseThrow(() -> new UnknownError("The team to inclue does not exist."));

            LocalSharedTeam.Builder newTargetTeam = targetTeam.toBuilder();

            Map<String, ByteString> emptyMembers = teamToInclude.toBuilder().getMembersMap();
            emptyMembers.replaceAll((key, value) -> ByteString.EMPTY);

            newTargetTeam
                    .putAllMembers(emptyMembers);

            saveTeams();
        }

        @Override
        public void createTeam(String teamName, byte @Nullable [] defaultData) {
            if (getTeam(teamName).isPresent()) {
                return;
            }

            LocalSharedTeam.Builder newTeam = LocalSharedTeam.newBuilder();
            newTeam.setName(teamName);

            if (defaultData != null) {
                newTeam.setDefaultData(ByteString.copyFrom(defaultData));
            }

            teamData = teamData.toBuilder().addTeams(newTeam).build();

            saveTeams();
        }

        @Override
        public void deleteTeam(String teamName) {
            Optional<LocalSharedTeam> teamToDelete = getTeam(teamName);

            if (!teamToDelete.isPresent()) {
                return;
            }

            teamData = teamData.toBuilder().removeTeams(teamData.getTeamsList().indexOf(teamToDelete.get())).build();

            saveTeams();
        }
    }

    /**
     * Caching the results of these methods by the implementer is highly recommended.
     * Gets all the team names the player is in.
     *
     * @param playerId The player's id, we recommend using {@link Player#getUniqueId()}, if this is usable.
     */
    String[] getTeamNamesForPlayer(String playerId);

    /**
     * List all the teams.
     *
     * @return The names of all the created teams.
     */
    String[] listTeams();

    /**
     * Caching the results of these methods by the implementer is highly recommended.
     * Returns the attached data of a player in a team.
     *
     * @param playerId The player's id, we recommend using {@link Player#getUniqueId()}, if this is usable.
     * @param teamName The team's name.
     * @return The player's data for the team.
     * @throws UnknownError If the team does not exist.
     */
    byte[] accessPlayerDataForTeam(String playerId, String teamName) throws UnknownError;

    /**
     * @param playerId            The player's id, we recommend using {@link Player#getUniqueId()}, if this is usable.
     * @param teamName            The team's name.
     * @param deletedAttachedData Should the player's data be read and given back to the caller of this method?
     *                            Returns an empty array if not or if the player was already not in the team.
     * @return The player's data for the team.
     */
    @SuppressWarnings("UnusedReturnValue")
    byte[] removePlayerFromTeam(String playerId, String teamName, boolean deletedAttachedData) throws UnknownError;

    /**
     * @param playerIds All affected player's id, we recommend using {@link Player#getUniqueId()}, if this is usable.
     * @param teamName  The team's name.
     * @throws UnknownError {@code teamName} does not exist
     */
    void bulkRemovePlayersFromTeam(String[] playerIds, String teamName) throws UnknownError;

    /**
     * Adds a single player to a team without attachable data.
     *
     * @param playerId The player's id, we recommend using {@link Player#getUniqueId()}, if this is usable.
     * @param teamName The team's name.
     * @throws UnknownError {@code teamName} does not exist
     */
    default void addPlayerToTeam(String playerId, String teamName) throws UnknownError {
        addPlayerToTeam(playerId, teamName, null);
    }

    /**
     * Adds a single player to a team with attachable data.
     *
     * @param playerId The player's id, we recommend using {@link Player#getUniqueId()}, if this is usable.
     * @param teamName The team's name.
     * @param data     The player's data for the team.
     * @throws UnknownError {@code teamName} does not exist
     */
    void addPlayerToTeam(String playerId, String teamName, byte[] data) throws UnknownError;

    /**
     * Adds multiple players to a team with attached data.
     *
     * @param playerData All affected player's id assigned to the data for each of them, we recommend using {@link Player#getUniqueId()}, if this is usable.
     * @param teamName   The team's name.
     * @throws UnknownError {@code teamName} does not exist
     */
    void bulkAddPlayersToTeamWithData(Map<String, byte[]> playerData, String teamName) throws UnknownError;

    /**
     * Adds multiple players to a team without attaching any data to them
     *
     * @param playerData All affected player's id, we recommend using {@link Player#getUniqueId()}, if this is usable.
     * @param teamName   The team's name.
     * @throws UnknownError {@code teamName} does not exist
     */
    void bulkAddPlayersToTeamWithoutData(String[] playerData, String teamName) throws UnknownError;

    /**
     * Adds all the members of {@code teamToInclude} to {@code targetTeamName}.
     * WARNING: This method does not affect {@code teamToInclude}.
     * Future changes to {@code teamToInclude} will not affect {@code targetTeamName}.
     *
     * @param targetTeamName The name of the team.
     * @param teamToInclude  The name of the team to include in the other team.
     * @throws UnknownError {@code teamToInclude} or {@code targetTeamName} does not exist
     */
    void includeTeamMembersInAnotherTeam(String targetTeamName, String teamToInclude);

    /**
     * Creates a new empty team
     *
     * @param teamName The team's name.
     */
    default void createTeam(String teamName) {
        createTeam(teamName, null);
    }

    /**
     * Creates a new empty team
     *
     * @param teamName    The team's name.
     * @param defaultData The default data to attach to the team.
     */
    void createTeam(String teamName, byte @Nullable [] defaultData);

    /**
     * Deletes a team via its name
     *
     * @param teamName The team's name
     */
    void deleteTeam(String teamName);
}
