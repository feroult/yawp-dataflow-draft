package flowt.models.result;

public enum Status {
    APPROVED {
        @Override
        public Integer defaultMax() {
            return 10;
        }

        @Override
        public Integer defaultMin() {
            return 7;
        }
    }, EXAMS {
        @Override
        public Integer defaultMax() {
            return 6;
        }

        @Override
        public Integer defaultMin() {
            return 3;
        }
    }, REPROVED {
        @Override
        public Integer defaultMax() {
            return 2;
        }

        @Override
        public Integer defaultMin() {
            return 0;
        }
    };

    public abstract Integer defaultMax();

    public abstract Integer defaultMin();
}
