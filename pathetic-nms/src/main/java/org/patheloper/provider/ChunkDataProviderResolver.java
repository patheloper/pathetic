package org.patheloper.provider;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.patheloper.api.snapshot.ChunkDataProvider;
import org.patheloper.provider.paper.PaperChunkDataProvider;
import org.patheloper.provider.v1_12.v1_12ChunkDataProviderImpl;
import org.patheloper.provider.v1_15.v1_15ChunkDataProviderImpl;
import org.patheloper.provider.v1_16.v1_16ChunkDataProviderImpl;
import org.patheloper.provider.v1_17.v1_17ChunkDataProviderImpl;
import org.patheloper.provider.v1_8.v1_8ChunkDataProviderImpl;

@Getter
@Slf4j
public class ChunkDataProviderResolver {

  private final ChunkDataProvider chunkDataProvider;

  public ChunkDataProviderResolver(int major, int minor) {










    String version = "1." + major + "." + minor;

    if (isPaper()) {
      chunkDataProvider = new PaperChunkDataProvider();
    } else {
      chunkDataProvider = determineChunkDataProvider(major, minor);
    }

    log.info(
      "Detected version v{}, using {}", version, chunkDataProvider.getClass().getSimpleName());
  }

  private ChunkDataProvider determineChunkDataProvider(int major, int minor) {










    final ChunkDataProvider chunkDataProvider;
    switch (major) {
      case 21:
      case 20:
      case 19:
      case 18:
        chunkDataProvider = new v1_18UpChunkDataProviderImpl();
        break;
      case 17:
        chunkDataProvider = new v1_17ChunkDataProviderImpl();
        break;
      case 16:
        chunkDataProvider = new v1_16ChunkDataProviderImpl();
        break;
      case 15:
        chunkDataProvider = new v1_15ChunkDataProviderImpl();
        break;
      case 12:
        chunkDataProvider = new v1_12ChunkDataProviderImpl();
        break;
      case 8:
        chunkDataProvider = new v1_8ChunkDataProviderImpl();
        break;
      default:
        throw new IllegalArgumentException("Unsupported version: " + major + "." + minor);
    }
    return chunkDataProvider;
  }

  private boolean isPaper() {










    try {
      Class.forName("io.papermc.paper.configuration.GlobalConfiguration");
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }
}
