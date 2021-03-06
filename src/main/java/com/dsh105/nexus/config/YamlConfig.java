/*
 * This file is part of Nexus.
 *
 * Nexus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Nexus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Nexus.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dsh105.nexus.config;

import com.dsh105.nexus.Nexus;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public class YamlConfig {

    protected LinkedHashMap<String, Object> options = new LinkedHashMap<>();
    private String fileName;

    public YamlConfig(String fileName) {
        this.fileName = fileName;
    }

    public void setDefaults() {
    }

    public String getFileName() {
        return fileName;
    }

    public void save() {
        PrintWriter writer = null;
        try {
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            writer = new PrintWriter(file);
            writer.write(new Yaml().dump(this.options));
        } catch (IOException e) {
            Nexus.LOGGER.severe("Failed to save configuration file: " + fileName);
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public void load() {
        FileInputStream input = null;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            input = new FileInputStream(file);
            Yaml yaml = new Yaml();
            Map<String, Object> loaded = (Map<String, Object>) yaml.load(input);
            if (loaded != null) {
                this.loadData(loaded);
            }
        } catch (IOException e) {
            Nexus.LOGGER.severe("Failed to load configuration file: " + fileName);
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Nexus.LOGGER.severe("Failed to load configuration file: " + fileName);
                    e.printStackTrace();
                }
            }
        }
        this.save();
    }

    public void clearFile() {
        File file = new File(fileName);
        if (!file.exists()) {
            file.delete();
        }
    }

    public void loadData(Map<String, Object> loadedData) {
        for (String key : loadedData.keySet()) {
            this.options.put(key, loadedData.get(key));
        }
    }

    public void set(String path, Object value) {
        this.options.put(path, value);
    }

    public Object get(String path) {
        return options.get(path);
    }

    public <T> T get(String path, T defaultValue) {
        Object value = this.get(path);
        if (value != null && defaultValue.getClass().isInstance(value)) {
            return (T) value;
        }
        return defaultValue;
    }
}