package org.patheloper.provider;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.patheloper.api.snapshot.ChunkDataProvider;
import org.patheloper.provider.v1_12.OneTwelveChunkDataProviderImpl;
import org.patheloper.provider.v1_15.OneFifteenChunkDataProviderImpl;
import org.patheloper.provider.v1_16.OneSixteenChunkDataProviderImpl;
import org.patheloper.provider.v1_17.OneSeventeenChunkDataProviderImpl;
import org.patheloper.provider.v1_18.OneEighteenChunkDataProviderImpl;
import org.patheloper.provider.v1_18_R2.OneEighteenTwoChunkDataProviderImpl;
import org.patheloper.provider.v1_19_R2.OneNineteenTwoChunkDataProviderImpl;
import org.patheloper.provider.v1_19_R3.OneNineteenThreeChunkDataProviderImpl;
import org.patheloper.provider.v1_20_R1.OneTwentyOneChunkDataProviderImpl;
import org.patheloper.provider.v1_20_R2.OneTwentyTwoChunkDataProviderImpl;
import org.patheloper.provider.v1_20_R3.OneTwentyThreeChunkDataProviderImpl;
import org.patheloper.provider.v1_20_R4.OneTwentyFourChunkDataProviderImpl;
import org.patheloper.provider.v1_8.OneEightChunkDataProviderImpl;
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

    log.info(
        "Detected version v{}, using {}", version, chunkDataProvider.getClass().getSimpleName());
  }

  private ChunkDataProvider determineChunkDataProvider(int major, int minor) {
    final ChunkDataProvider chunkDataProvider;
    switch (major) {
      case 21:
        chunkDataProvider = new OneTwentyFourChunkDataProviderImpl();
        break;
      case 20:
        if (minor == 5 || minor == 6) {
          chunkDataProvider = new OneTwentyFourChunkDataProviderImpl();
          break;
        } else if (minor == 3 || minor == 4) {
          chunkDataProvider = new OneTwentyThreeChunkDataProviderImpl();
          break;
        } else if (minor == 2) {
          chunkDataProvider = new OneTwentyTwoChunkDataProviderImpl();
          break;
        } else if (minor == 1) {
          chunkDataProvider = new OneTwentyOneChunkDataProviderImpl();
          break;
        }
        throw new IllegalArgumentException("Unsupported version: " + major + "." + minor);
      case 19:
        if (minor == 2 || minor == 3) {
          chunkDataProvider = new OneNineteenTwoChunkDataProviderImpl();
          break;
        }
        if (minor == 4) {
          chunkDataProvider = new OneNineteenThreeChunkDataProviderImpl();
          break;
        }
        throw new IllegalArgumentException("Unsupported version: " + major + "." + minor);
      case 18:
        if (minor == 2) {
          chunkDataProvider = new OneEighteenTwoChunkDataProviderImpl();
          break;
        }
        chunkDataProvider = new OneEighteenChunkDataProviderImpl();
        break;
      case 17:
        chunkDataProvider = new OneSeventeenChunkDataProviderImpl();
        break;
      case 16:
        chunkDataProvider = new OneSixteenChunkDataProviderImpl();
        break;
      case 15:
        chunkDataProvider = new OneFifteenChunkDataProviderImpl();
        break;
      case 12:
        chunkDataProvider = new OneTwelveChunkDataProviderImpl();
        break;
      case 8:
        chunkDataProvider = new OneEightChunkDataProviderImpl();
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
