package se.fusion1013.plugin.cobalt.util;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class HexUtils {

    private static final Pattern RAINBOW_PATTERN = Pattern.compile("<(rainbow|r)(:\\d*\\.?\\d+){0,2}>");
    private static final Pattern GRADIENT_PATTERN = Pattern.compile("<(gradient|g)(:#([A-Fa-f0-9]){6})*>");
    private static final List<Pattern> HEX_PATTERNS = Arrays.asList(
            Pattern.compile("<#([A-Fa-f0-9]){6}>"),
            Pattern.compile("&#([A-Fa-f0-9]){6}"),
            Pattern.compile("#([A-Fa-f0-9]){6}")
    );

    private static final Pattern STOP = Pattern.compile("<(gradient|g)(:#([A-Fa-f0-9]){6})*>|<(rainbow|r)(:\\d*\\.?\\d+){0,2}>|(&[a-f0-9r])|<#([A-Fa-f0-9]){6}>|&#([A-Fa-f0-9]){6}|#([A-Fa-f0-9]){6}|" + org.bukkit.ChatColor.COLOR_CHAR);

    /**
     * Sends a CommandSender a colored message
     *
     * @param sender The CommandSender to send to
     * @param message The message to send
     */
    public static void sendMessage(Player sender, String message){
        sender.sendMessage(colorify(message));
    }

    /**
     * Parses gradients, hex colors and legacy color codes
     *
     * @param message The message
     * @return A color-replaced message
     */
    public static String colorify(String message){
        String parsed = message;
        parsed = parseRainbow(parsed);
        parsed = parseGradients(parsed);
        parsed = parseHex(parsed);
        parsed = parseLegacy(parsed);
        return parsed;
    }

    private static String parseRainbow(String message){
        String parsed = message;

        Matcher matcher = RAINBOW_PATTERN.matcher(parsed);
        while(matcher.find()){
            StringBuilder parsedRainbow = new StringBuilder();

            String match = matcher.group();
            int tagLength = match.startsWith("<ra") ? 8 : 2;

            int indexOfClose = match.indexOf(">");
            String extraDataContent = match.substring(tagLength, indexOfClose);

            double[] extraData;
            if (!extraDataContent.isEmpty()){
                extraDataContent = extraDataContent.substring(1);
                extraData = Arrays.stream(extraDataContent.split(":")).mapToDouble(Double::parseDouble).toArray();
            } else {
                extraData = new double[0];
            }

            float saturation = extraData.length > 0 ? (float) extraData[0] : 1.0F;
            float brightness = extraData.length > 1 ? (float) extraData[1] : 1.0F;

            int stop = findStop(parsed, matcher.end());
            String content = parsed.substring(matcher.end(), stop);
            Rainbow rainbow = new Rainbow(content.length(), saturation, brightness);

            for (char c : content.toCharArray()){
                parsedRainbow.append(translateHex(rainbow.next())).append(c);
            }

            String before = parsed.substring(0, matcher.start());
            String after = parsed.substring(stop);
            parsed = before + parsedRainbow + after;
            matcher = RAINBOW_PATTERN.matcher(parsed);
        }

        return parsed;
    }

    private static String parseGradients(String message){
        String parsed = message;

        Matcher matcher = GRADIENT_PATTERN.matcher(parsed);
        while (matcher.find()){
            StringBuilder parsedGradient = new StringBuilder();

            String match = matcher.group();
            int tagLength = match.startsWith("<gr") ? 10 : 3;

            int indexOfClose = match.indexOf(">");
            String hexContent = match.substring(tagLength, indexOfClose);
            List<Color> hexSteps = Arrays.stream(hexContent.split(":")).map(Color::decode).collect(Collectors.toList());

            int stop = findStop(parsed, matcher.end());
            String content = parsed.substring(matcher.end(), stop);
            Gradient gradient = new Gradient(hexSteps, content.length());

            for (char c : content.toCharArray()){
                parsedGradient.append(translateHex(gradient.next())).append(c);
            }

            String before = parsed.substring(0, matcher.start());
            String after = parsed.substring(stop);
            parsed = before + parsedGradient + after;
            matcher = GRADIENT_PATTERN.matcher(parsed);
        }

        return parsed;
    }

    private static String parseHex(String message){
        String parsed = message;

        for (Pattern pattern : HEX_PATTERNS){
            Matcher matcher = pattern.matcher(parsed);
            while (matcher.find()){
                String color = translateHex(cleanHex(matcher.group()));
                String before = parsed.substring(0, matcher.start());
                String after = parsed.substring(matcher.end());
                parsed = before + color + after;
                matcher = pattern.matcher(parsed);
            }
        }

        return parsed;
    }

    private static String parseLegacy(String message){
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Returns the index before the color changes
     *
     * @param content The content to search through
     * @param searchAfter The index at which to search after
     * @return The index of the color sotp, or the end of the string index if none is found
     */
    private static int findStop(String content, int searchAfter){
        Matcher matcher = STOP.matcher(content);
        while (matcher.find()){
            if (matcher.start() > searchAfter){
                return matcher.start();
            }
        }
        return content.length();
    }

    private static String cleanHex(String hex){
        if (hex.startsWith("<")){
            return hex.substring(1, hex.length() - 1);
        } else if (hex.startsWith("&")){
            return hex.substring(1);
        } else {
            return hex;
        }
    }

    /**
     * Finds the closest hex or ChatColor value as the hex string
     *
     * @param hex The hex color
     * @return The closest ChatColor value
     */
    private static String translateHex(String hex){
        return ChatColor.of(hex).toString();
    }

    private static String translateHex(Color color){
        return ChatColor.of(color).toString();
    }

    /**
     * Allows generation of a multi-part gradient with a fixed number of steps
     */
    public static class Gradient{

        private final List<Color> colors;
        private final int stepSize;
        private int step, stepIndex;

        public Gradient(List<Color> colors, int totalColors){
            if (colors.size() < 2){
                throw new IllegalArgumentException("Must provide at least 2 colors");
            }

            if (totalColors < 1){
                throw new IllegalArgumentException("Must have at least 1 total color");
            }

            this.colors = colors;
            this.stepSize = totalColors / (colors.size() - 1);
            this.step = this.stepIndex = 0;
        }

        public Color next(){
            Color color;
            if (this.stepIndex + 1 < this.colors.size()){
                Color start = this.colors.get(this.stepIndex);
                Color end = this.colors.get(this.stepIndex + 1);
                float interval = (float) this.step / this.stepSize;

                color = getGradientInterval(start, end, interval);
            } else {
                color = this.colors.get(this.colors.size() - 1);
            }

            this.step += 1;
            if (this.step >= this.stepSize){
                this.step = 0;
                this.stepIndex++;
            }

            return color;
        }

        public static Color getGradientInterval(Color start, Color end, float interval){
            if (0 > interval || interval > 1){
                throw new IllegalArgumentException("Interval must be between 0 and 1 inclusively.");
            }

            int r = (int) (end.getRed() * interval + start.getRed() * (1 - interval));
            int g = (int) (end.getGreen() * interval + start.getGreen() * (1 - interval));
            int b = (int) (end.getBlue() * interval + start.getBlue() * (1 - interval));

            return new Color(r, g, b);
        }
    }

    public static class Rainbow{
        private final float hueStep, saturation, brightness;
        private float hue;

        public Rainbow(int totalColors, float saturation, float brightness){
            if (totalColors < 1){
                throw new IllegalArgumentException("Must have at least 1 total color");
            }

            if (0.0F > saturation || saturation > 1.0F){
                throw new IllegalArgumentException("Saturation must be between 0.0 and 1.0");
            }

            if (0.0F > brightness || brightness > 1.0F){
                throw new IllegalArgumentException("Saturation must be between 0.0 and 1.0");
            }

            this.hueStep = 1.0F / totalColors;
            this.saturation = saturation;
            this.brightness = brightness;
            this.hue = 0;
        }

        public Rainbow(int totalColors){
            this(totalColors, 1.0F, 1.0F);
        }

        public Color next(){
            Color color = Color.getHSBColor(this.hue, this.saturation, this.brightness);
            this.hue += this.hueStep;
            return color;
        }
    }
}
