package se.fusion1013.plugin.cobaltmagick.database.musicbox;

import se.fusion1013.plugin.cobaltcore.database.system.IDao;
import se.fusion1013.plugin.cobaltmagick.world.structures.MusicBox;

import java.util.Map;

public interface IMusicBoxDao extends IDao {

    /**
     * Updates the message of a <code>MusicBox</code>.
     *
     * @param boxId the id of the <code>MusicBox</code>.
     * @param msg the new message.
     */
    void updateMusicBoxMessageAsync(int boxId, String msg);

    /**
     * Updates the message of a <code>MusicBox</code>.
     *
     * @param boxId the id of the <code>MusicBox</code>.
     * @param msg the new message.
     */
    void updateMusicBoxMessageSync(int boxId, String msg);

    /**
     * Gets a map of all the <code>MusicBox</code>'s in the database.
     *
     * @return a map of <code>MusicBox</code>'s.
     */
    Map<String, MusicBox> getMusicBoxes();

    /**
     * Inserts a <code>MusicBox</code> into the database.
     *
     * @param musicBox the <code>MusicBox</code> to insert into the database.
     */
    void insertMusicBoxAsync(MusicBox musicBox);

    /**
     * Inserts a <code>MusicBox</code> into the database.
     *
     * @param musicBox the <code>MusicBox</code> to insert into the database.
     */
    void insertMusicBoxSync(MusicBox musicBox);

    /**
     * Removes a <code>MusicBox</code> from the database.
     * @param musicBox
     */
    void removeMusicBoxAsync(MusicBox musicBox);

    /**
     * Removes a <code>MusicBox</code> from the database.
     * @param musicBox
     */
    void removeMusicBoxSync(MusicBox musicBox);

    @Override
    default String getId() { return "music_box"; }

}
