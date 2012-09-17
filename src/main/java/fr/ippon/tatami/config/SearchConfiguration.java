package fr.ippon.tatami.config;

import fr.ippon.tatami.config.elasticsearch.ElasticSearchServerNodeFactory;
import fr.ippon.tatami.config.elasticsearch.ElasticSearchSettings;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequest;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.client.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

/**
 * Search configuration : uses Elastic Search if it is configured, basic Lucene otherwise.
 */
@Configuration
public class SearchConfiguration {

    private final Log log = LogFactory.getLog(SearchConfiguration.class);

    @Inject
    private Environment env;

    // ElasticSearch or Lucene configuration ?

    @Bean(name = "elasticsearchActivated")
    public boolean elasticsearchActivated() {
        return env.getProperty("elasticsearch.enabled", Boolean.class);
    }

    // ElasticSearch configuration

    @Bean
    public ElasticSearchSettings esSettings() {
        if (elasticsearchActivated()) {
            String configPath = env.getRequiredProperty("elasticsearch.path.conf");
            ElasticSearchSettings settings = null;
            if (StringUtils.isBlank(configPath)) {
                settings = new ElasticSearchSettings();
            } else {
                settings = new ElasticSearchSettings(configPath);
            }
            return settings;
        } else {
            return null;
        }
    }

    @Bean(name = "nodeFactory")
    public ElasticSearchServerNodeFactory nodeFactory() {
        if (elasticsearchActivated()) {
            final ElasticSearchServerNodeFactory factory = new ElasticSearchServerNodeFactory();
            factory.setEsSettings(esSettings());
            factory.setIndexName(indexName());
            factory.setElasticsearchActivated(elasticsearchActivated());
            return factory;
        } else {
            return null;
        }
    }

    @Bean
    @DependsOn("nodeFactory")
    public Client client() {
        if (elasticsearchActivated()) {
            log.info("Elasticsearch is activated, initializing client connection...");
            final Client client = nodeFactory().getServerNode().client();

            if (log.isDebugEnabled()) {
                final NodesInfoResponse nir =
                        client.admin().cluster().nodesInfo(new NodesInfoRequest()).actionGet();

                log.debug("Client is now connected to the " + nir.nodes().length + " nodes cluster named "
                        + nir.clusterName());
            }
            return client;
        } else {
            log.warn("Elastic Search is NOT activated  : no client instantiated!");
            return null;
        }
    }

    @Bean
    public String indexName() {
        return env.getRequiredProperty("elasticsearch.indexName");
    }

    // Lucene configuration

    @Bean
    public Analyzer analyzer() {
        if (!elasticsearchActivated()) {
            Analyzer analyzer = null;
            String language = env.getRequiredProperty("lucene.language");
            if (language.equals("French")) {
                analyzer = new FrenchAnalyzer(Version.LUCENE_36);
            } else {
                analyzer = new StandardAnalyzer(Version.LUCENE_36);
            }
            return analyzer;
        } else {
            return null;
        }
    }

    @Bean(name = "statusDirectory")
    public Directory statusDirectory() {
        return internalDirectory("status");
    }

    @Bean(name = "userDirectory")
    public Directory userDirectory() {
        return internalDirectory("user");
    }

    private Directory internalDirectory(String directoryName) {
        if (!elasticsearchActivated()) {
            log.info("Initializing Lucene " + directoryName + " directory");
            String lucenePath = env.getRequiredProperty("lucene.path");
            try {
                Directory directory = new NIOFSDirectory(
                        new File(lucenePath + System.getProperty("file.separator") + directoryName));

                log.info("Lucene directory " + directoryName + " is initialized");
                return directory;
            } catch (IOException e) {
                log.error("Lucene direcotry could not be started : " + e.getMessage());
                if (log.isWarnEnabled()) {
                    e.printStackTrace();
                }
                return null;
            }
        } else {
            return null;
        }
    }

    @Bean(name = "statusIndexWriter")
    @DependsOn({"statusDirectory"})
    public IndexWriter statusIndexWriter() {
        Directory directory = statusDirectory();
        return internalIndexWriter(directory);
    }

    @Bean
    @DependsOn({"userDirectory"})
    public IndexWriter userIndexWriter() {
        Directory directory = userDirectory();
        return internalIndexWriter(directory);
    }

    private IndexWriter internalIndexWriter(Directory directory) {
        if (!elasticsearchActivated()) {
            try {
                if (directory != null) {
                    Analyzer analyzer = analyzer();
                    IndexWriterConfig indexWriterConfig =
                            new IndexWriterConfig(Version.LUCENE_36, analyzer);

                    IndexWriter indexWriter = new IndexWriter(directory,
                            indexWriterConfig);

                    return indexWriter;
                } else {
                    return null;
                }
            } catch (IOException e) {
                log.error("Lucene I/O error while writing : " + e.getMessage());
                if (log.isInfoEnabled()) {
                    e.printStackTrace();
                }
                return null;
            }
        } else {
            return null;
        }
    }

    @Bean
    @DependsOn({"statusIndexWriter"})
    public SearcherManager statusSearcherManager() {
        return internalSearcherManager(statusIndexWriter());
    }

    @Bean
    @DependsOn({"userIndexWriter"})
    public SearcherManager userSearcherManager() {
        return internalSearcherManager(userIndexWriter());
    }

    private SearcherManager internalSearcherManager(IndexWriter indexWriter) {
        if (!elasticsearchActivated()) {
            try {
                if (indexWriter != null) {
                    SearcherManager searcherManager = new SearcherManager(indexWriter, true, null);
                    return searcherManager;
                } else {
                    return null;
                }
            } catch (IOException e) {
                log.error("Lucene I/O error wnile reading : " + e.getMessage());
                if (log.isInfoEnabled()) {
                    e.printStackTrace();
                }
                return null;
            }
        } else {
            return null;
        }
    }
}
