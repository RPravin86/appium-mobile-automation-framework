package io.github.rpravin86.mobile.evidence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class VideoCompressor {

    private static final Logger LOGGER = LogManager.getLogger(VideoCompressor.class);
    private static final Duration PROCESS_TIMEOUT = Duration.ofMinutes(3);

    private VideoCompressor() {
    }

    public static Path compress(Path source, EvidenceConfig config) {
        Path destination = EvidencePaths.compressedVideo(source);
        List<String> command = List.of(
                "ffmpeg",
                "-y",
                "-i", source.toString(),
                "-vf", "scale=" + config.videoMaxWidth() + ":-2:force_original_aspect_ratio=decrease",
                "-c:v", "libx264",
                "-preset", "medium",
                "-crf", Integer.toString(config.videoCrf()),
                "-an",
                "-movflags", "+faststart",
                destination.toString()
        );

        try {
            Process process = new ProcessBuilder(command)
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .start();
            boolean completed = process.waitFor(PROCESS_TIMEOUT.toSeconds(), TimeUnit.SECONDS);
            if (!completed) {
                process.destroyForcibly();
                LOGGER.warn("Video compression exceeded {} seconds; retaining raw recording", PROCESS_TIMEOUT.toSeconds());
                return source;
            }
            if (process.exitValue() != 0 || !Files.isRegularFile(destination) || Files.size(destination) == 0) {
                Files.deleteIfExists(destination);
                LOGGER.warn("FFmpeg did not produce a usable compressed recording; retaining raw recording");
                return source;
            }
            if (!config.keepRawVideo()) {
                Files.deleteIfExists(source);
            }
            LOGGER.info("Compressed screen recording to {}", destination);
            return destination;
        } catch (IOException exception) {
            LOGGER.warn("FFmpeg is unavailable; retaining raw screen recording at {}", source);
            return source;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            LOGGER.warn("Video compression was interrupted; retaining raw screen recording at {}", source);
            return source;
        }
    }
}
