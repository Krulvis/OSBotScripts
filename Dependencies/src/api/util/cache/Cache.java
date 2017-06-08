package api.util.cache;

import api.wrappers.ItemDef;

import java.io.File;
import java.io.RandomAccessFile;


public class Cache {

    private final String cachePath;
    private final RandomAccessFile mainData;
    private final Decompressor idx255;
    private static CacheLoader[] indexFiles;

    public Cache(final String cachePath_, final String cacheName) throws Exception {
        this.cachePath = cachePath_;
        mainData = new RandomAccessFile(cachePath + File.separator + cacheName + ".dat2", "rw");
        System.out.println("Succesfully loaded main cache data file!");
        idx255 = new Decompressor(255, mainData, new RandomAccessFile(cachePath + cacheName + ".idx255", "r"));
        int idxsCount = idx255.getArchivesCount();
        indexFiles = new CacheLoader[idxsCount];
        for (int id = 0; id < idxsCount; id++) {
            CacheLoader index = new CacheLoader(idx255, new Decompressor(id, mainData, new RandomAccessFile(cachePath + cacheName + ".idx" + id, "r")));
            System.out.println("Succesfully loaded cache index: " + id);
            if (index.getTable() == null) {
                continue;
            }
            indexFiles[id] = index;
        }
        ItemDef.setupCacheLoader(indexFiles[2]);
    }

    public static final String findCacheDirectory(final String rsLoadedHome) {
        final String home = rsLoadedHome + File.separator;
        final String cacheLocation = home + "jagexcache" + File.separator + "oldschool" + File.separator + "LIVE" + File.separator;
        System.out.println("Found cache directory: " + cacheLocation);
        return cacheLocation;
    }

    public CacheLoader[] getIndexFiles() {
        return indexFiles;
    }
    /*
	 * public static void fetchCachePath() { String osname; try { osname =
	 * System.getProperty("os.name"); } catch (Exception e) { osname =
	 * "Unknown"; }
	 * 
	 * osname = osname.toLowerCase();
	 * 
	 * try { userhome = System.getProperty("user.home"); if (userhome != null) {
	 * userhome += "/"; } } catch (Exception e) {
	 * 
	 * }
	 * 
	 * try { if (osname.startsWith("win")) { if (userhome == null) { userhome =
	 * System.getenv("USERPROFILE"); } } else if (userhome == null) { userhome =
	 * System.getenv("HOME"); }
	 * 
	 * if (userhome != null) { userhome += "/"; } } catch (Exception var17) { ;
	 * }
	 * 
	 * if (userhome == null) { userhome = "~/"; }
	 * 
	 * String[] filedX = new String[] { "c:/rscache/", "/rscache/",
	 * "c:/windows/", "c:/winnt/", "c:/", userhome, "/tmp/", "" }; String[]
	 * fieldC = new String[] { ".jagex_cache_0", ".file_store_0" }; }
	 */
}