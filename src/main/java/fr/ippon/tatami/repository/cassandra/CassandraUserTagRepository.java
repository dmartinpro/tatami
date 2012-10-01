package fr.ippon.tatami.repository.cassandra;

import fr.ippon.tatami.repository.UserTagRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

import static fr.ippon.tatami.config.ColumnFamilyKeys.USER_TAGS_CF;

/**
 * Cassandra implementation of the TagFriend repository.
 * <p/>
 * Structure :
 * - Key = login
 * - Name = tag + domain
 * - Value = time
 *
 * @author Julien Dubois
 */
@Repository
public class CassandraUserTagRepository
        extends AbstractCassandraFriendRepository
        implements UserTagRepository {

    @Override
    public void addTag(String domain, String login, String friendTag) {
        super.addFriend(login, getKey(domain, friendTag));
    }

    @Override
    public void removeTag(String domain, String login, String friendTag) {
        super.removeFriend(login, getKey(domain, friendTag));
    }

    @Override
    public Collection<String> findTags(String domain, String login) {
        return super.findFriends(getKey(domain, login));
    }

    @Override
    public String getFriendsCF() {
        return USER_TAGS_CF;
    }

    /**
     * Generates the key for this column family.
     */
    private String getKey(String domain, String tag) {
        return tag.toLowerCase() + "-" + domain;
    }
}
