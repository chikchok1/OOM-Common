package common.model;

public class ReservedRoomModel {

    /**
     * 예약 정보를 나타내는 불변 클래스
     * 한 번 생성되면 수정할 수 없음
     */
    public static class Reservation {
        private final String name;
        private final String room;
        private final String day;
        private final String period;
        private final String purpose;

        public Reservation(String name, String room, String day, String period, String purpose) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("name은 null이거나 공백일 수 없습니다.");
            }
            if (room == null || room.trim().isEmpty()) {
                throw new IllegalArgumentException("room은 null이거나 공백일 수 없습니다.");
            }
            if (day == null || day.trim().isEmpty()) {
                throw new IllegalArgumentException("day는 null이거나 공백일 수 없습니다.");
            }
            if (period == null || period.trim().isEmpty()) {
                throw new IllegalArgumentException("period는 null이거나 공백일 수 없습니다.");
            }
            if (purpose == null || purpose.trim().isEmpty()) {
                throw new IllegalArgumentException("purpose는 null이거나 공백일 수 없습니다.");
            }

            this.name = name;
            this.room = room;
            this.day = day;
            this.period = period;
            this.purpose = purpose;
        }

        // Getter 메서드들 (수정 불가)
        public String getName() {
            return name;
        }

        public String getRoom() {
            return room;
        }

        public String getDay() {
            return day;
        }

        public String getPeriod() {
            return period;
        }

        public String getPurpose() {
            return purpose;
        }

        @Override
        public String toString() {
            return "Reservation{" +
                    "name='" + name + '\'' +
                    ", room='" + room + '\'' +
                    ", day='" + day + '\'' +
                    ", period='" + period + '\'' +
                    ", purpose='" + purpose + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Reservation that = (Reservation) o;

            if (!name.equals(that.name)) return false;
            if (!room.equals(that.room)) return false;
            if (!day.equals(that.day)) return false;
            if (!period.equals(that.period)) return false;
            return purpose.equals(that.purpose);
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + room.hashCode();
            result = 31 * result + day.hashCode();
            result = 31 * result + period.hashCode();
            result = 31 * result + purpose.hashCode();
            return result;
        }
    }

    // 모델 기능은 서버에서 처리
}
