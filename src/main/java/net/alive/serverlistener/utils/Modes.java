package net.alive.serverlistener.utils;

    public enum Modes {
        SKYBLOCK("Skyblock", "cxnprice.mode.skyblock"),
        CITYBUILD("Citybuild", "cxnprice.mode.citybuild"),
        LOBBY("Lobby", "cxnprice.mode.lobby"),
        NOTHING("NOTHING", "cxnprice.mode.nothing");

        private final String text;
        private final String translationKey;

        Modes(final String text, final String translationKey) {
            this.text = text;
            this.translationKey = translationKey;
        }

        @Override
        public String toString() {
            return text;
        }

        public String getTranslationKey(){
            return translationKey;
        }
    }

