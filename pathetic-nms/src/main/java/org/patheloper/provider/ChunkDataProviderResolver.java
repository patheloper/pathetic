package org.patheloper.provider;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.patheloper.api.snapshot.ChunkDataProvider;
import org.patheloper.provider.v1_16.v1_16ChunkDataProviderImpl;
import org.patheloper.provider.v1_12.v1_12ChunkDataProviderImpl;
import org.patheloper.provider.v1_15.v1_15ChunkDataProviderImpl;
import org.patheloper.provider.v1_17.v1_17ChunkDataProviderImpl;
import org.patheloper.provider.v1_18.v1_18ChunkDataProviderImpl;
import org.patheloper.provider.v1_18_R2.v1_18_R2ChunkDataProviderImpl;
import org.patheloper.provider.v1_19_R2.v1_19_R2ChunkDataProviderImpl;
import org.patheloper.provider.v1_19_R3.v1_19_R3ChunkDataProviderImpl;
import org.patheloper.provider.v1_20_R1.v1_20_R1ChunkDataProviderImpl;
import org.patheloper.provider.v1_20_R2.v1_20_R2ChunkDataProviderImpl;
import org.patheloper.provider.v1_20_R3.v1_20_R3ChunkDataProviderImpl;
import org.patheloper.provider.v1_20_R4.v1_20_R4ChunkDataProviderImpl;
import org.patheloper.provider.v1_21_R1.v1_21_R1ChunkDataProviderImpl;
import org.patheloper.provider.v1_21_R2.v1_21_R2ChunkDataProviderImpl;
import org.patheloper.provider.v1_8.v1_8ChunkDataProviderImpl;
import org.patheloper.provider.paper.PaperChunkDataProvider;

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

    log.debug(
        "Detected version v{}, using {}", version, chunkDataProvider.getClass().getSimpleName());
  }

  private ChunkDataProvider determineChunkDataProvider(int major, int minor) {
    final ChunkDataProvider chunkDataProvider;
    switch (major) {
      case 21:
        if(minor == 2) {
          chunkDataProvider = new v1_21_R2ChunkDataProviderImpl();
          break;
        }
        chunkDataProvider = new v1_21_R1ChunkDataProviderImpl();
        break;
      case 20:
        if (minor == 5 || minor == 6) {
          chunkDataProvider = new v1_20_R4ChunkDataProviderImpl();
          break;
        } else if (minor == 3 || minor == 4) {
          chunkDataProvider = new v1_20_R3ChunkDataProviderImpl();
          break;
        } else if (minor == 2) {
          chunkDataProvider = new v1_20_R2ChunkDataProviderImpl();
          break;
        } else if (minor == 1) {
          chunkDataProvider = new v1_20_R1ChunkDataProviderImpl();
          break;
        }
      case 19:
        if (minor == 2 || minor == 3) {
          chunkDataProvider = new v1_19_R2ChunkDataProviderImpl();
          break;
        }
        if (minor == 4) {
          chunkDataProvider = new v1_19_R3ChunkDataProviderImpl();
          break;
        }
      case 18:
        if (minor == 2) {
          chunkDataProvider = new v1_18_R2ChunkDataProviderImpl();
          break;
        }
        chunkDataProvider = new v1_18ChunkDataProviderImpl();
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
