package flowt.models.result;

public enum Status {
    APPROVED {
        @Override
        public Integer defaultThreshold() {
            return 7;
        }
    }, EXAMS {
        @Override
        public Integer defaultThreshold() {
            return 5;
        }
    }, REPROVED {
        @Override
        public Integer defaultThreshold() {
            return 0;
        }
    };

    public abstract Integer defaultThreshold();
}
