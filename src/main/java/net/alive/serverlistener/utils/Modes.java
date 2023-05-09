package net.alive.serverlistener.utils;

    public enum Modes {
        SKYBLOCK("Skyblock"),
        CITYBUILD("Citybuild"),
        LOBBY("Lobby"),
        NOTHING("NOTHING");

        private final String text;

        Modes(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

