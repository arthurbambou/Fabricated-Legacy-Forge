package cpw.mods.fml.common.versioning;

public class Restriction {
    private final ArtifactVersion lowerBound;
    private final boolean lowerBoundInclusive;
    private final ArtifactVersion upperBound;
    private final boolean upperBoundInclusive;
    public static final Restriction EVERYTHING = new Restriction(null, false, null, false);

    public Restriction(ArtifactVersion lowerBound, boolean lowerBoundInclusive, ArtifactVersion upperBound, boolean upperBoundInclusive) {
        this.lowerBound = lowerBound;
        this.lowerBoundInclusive = lowerBoundInclusive;
        this.upperBound = upperBound;
        this.upperBoundInclusive = upperBoundInclusive;
    }

    public ArtifactVersion getLowerBound() {
        return this.lowerBound;
    }

    public boolean isLowerBoundInclusive() {
        return this.lowerBoundInclusive;
    }

    public ArtifactVersion getUpperBound() {
        return this.upperBound;
    }

    public boolean isUpperBoundInclusive() {
        return this.upperBoundInclusive;
    }

    public boolean containsVersion(ArtifactVersion version) {
        if (this.lowerBound != null) {
            int comparison = this.lowerBound.compareTo(version);
            if (comparison == 0 && !this.lowerBoundInclusive) {
                return false;
            }

            if (comparison > 0) {
                return false;
            }
        }

        if (this.upperBound != null) {
            int comparison = this.upperBound.compareTo(version);
            if (comparison == 0 && !this.upperBoundInclusive) {
                return false;
            }

            if (comparison < 0) {
                return false;
            }
        }

        return true;
    }

    public int hashCode() {
        int result = 13;
        if (this.lowerBound == null) {
            ++result;
        } else {
            result += this.lowerBound.hashCode();
        }

        result *= this.lowerBoundInclusive ? 1 : 2;
        if (this.upperBound == null) {
            result -= 3;
        } else {
            result -= this.upperBound.hashCode();
        }

        return result * (this.upperBoundInclusive ? 2 : 3);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof Restriction)) {
            return false;
        } else {
            Restriction restriction = (Restriction)other;
            if (this.lowerBound != null) {
                if (!this.lowerBound.equals(restriction.lowerBound)) {
                    return false;
                }
            } else if (restriction.lowerBound != null) {
                return false;
            }

            if (this.lowerBoundInclusive != restriction.lowerBoundInclusive) {
                return false;
            } else {
                if (this.upperBound != null) {
                    if (!this.upperBound.equals(restriction.upperBound)) {
                        return false;
                    }
                } else if (restriction.upperBound != null) {
                    return false;
                }

                return this.upperBoundInclusive == restriction.upperBoundInclusive;
            }
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(this.isLowerBoundInclusive() ? "[" : "(");
        if (this.getLowerBound() != null) {
            buf.append(this.getLowerBound().toString());
        }

        buf.append(",");
        if (this.getUpperBound() != null) {
            buf.append(this.getUpperBound().toString());
        }

        buf.append(this.isUpperBoundInclusive() ? "]" : ")");
        return buf.toString();
    }
}
