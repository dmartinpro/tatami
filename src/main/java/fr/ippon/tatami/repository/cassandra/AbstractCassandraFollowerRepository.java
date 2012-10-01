package fr.ippon.tatami.repository.cassandra;

import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import static fr.ippon.tatami.config.ColumnFamilyKeys.FOLLOWERS_CF;

/**
 * Abstract class for managing followers : users who follow another user or a tag.
 */
public abstract class AbstractCassandraFollowerRepository {

    ColumnFamilyTemplate<String, String> template;

    @Inject
    private Keyspace keyspaceOperator;

    @PostConstruct
    public void init() {
        template = new ThriftColumnFamilyTemplate<String, String>(keyspaceOperator,
                getFollowersCF(),
                StringSerializer.get(),
                StringSerializer.get());
    }

    protected void addFollower(String key, String followerKey) {
        Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, StringSerializer.get());
        mutator.insert(key, FOLLOWERS_CF, HFactory.createColumn(followerKey,
                Calendar.getInstance().getTimeInMillis(), StringSerializer.get(), LongSerializer.get()));
    }

    protected void removeFollower(String key, String followerKey) {
        Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, StringSerializer.get());
        mutator.delete(key, FOLLOWERS_CF, followerKey, StringSerializer.get());
    }

    protected Collection<String> findFollowers(String key) {
        ColumnFamilyResult<String, String> result = template.queryColumns(key);
        Collection<String> followers = new ArrayList<String>();
        for (String columnName : result.getColumnNames()) {
            followers.add(columnName);
        }
        return followers;
    }

    protected abstract String getFollowersCF();
}
