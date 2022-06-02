package cpw.mods.fml.common.versioning;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class VersionRange {
    private final ArtifactVersion recommendedVersion;
    private final List<Restriction> restrictions;

    private VersionRange(ArtifactVersion recommendedVersion, List<Restriction> restrictions) {
        this.recommendedVersion = recommendedVersion;
        this.restrictions = restrictions;
    }

    public ArtifactVersion getRecommendedVersion() {
        return this.recommendedVersion;
    }

    public List<Restriction> getRestrictions() {
        return this.restrictions;
    }

    public VersionRange cloneOf() {
        List<Restriction> copiedRestrictions = null;
        if (this.restrictions != null) {
            copiedRestrictions = new ArrayList();
            if (!this.restrictions.isEmpty()) {
                copiedRestrictions.addAll(this.restrictions);
            }
        }

        return new VersionRange(this.recommendedVersion, copiedRestrictions);
    }

    public static VersionRange createFromVersionSpec(String spec) throws InvalidVersionSpecificationException {
        if (spec == null) {
            return null;
        } else {
            List<Restriction> restrictions = new ArrayList();
            String process = spec;
            ArtifactVersion version = null;
            ArtifactVersion upperBound = null;
            ArtifactVersion lowerBound = null;

            while(process.startsWith("[") || process.startsWith("(")) {
                int index1 = process.indexOf(")");
                int index2 = process.indexOf("]");
                int index = index2;
                if ((index2 < 0 || index1 < index2) && index1 >= 0) {
                    index = index1;
                }

                if (index < 0) {
                    throw new InvalidVersionSpecificationException("Unbounded range: " + spec);
                }

                Restriction restriction = parseRestriction(process.substring(0, index + 1));
                if (lowerBound == null) {
                    lowerBound = restriction.getLowerBound();
                }

                if (upperBound != null && (restriction.getLowerBound() == null || restriction.getLowerBound().compareTo(upperBound) < 0)) {
                    throw new InvalidVersionSpecificationException("Ranges overlap: " + spec);
                }

                restrictions.add(restriction);
                upperBound = restriction.getUpperBound();
                process = process.substring(index + 1).trim();
                if (process.length() > 0 && process.startsWith(",")) {
                    process = process.substring(1).trim();
                }
            }

            if (process.length() > 0) {
                if (restrictions.size() > 0) {
                    throw new InvalidVersionSpecificationException("Only fully-qualified sets allowed in multiple set scenario: " + spec);
                }

                version = new DefaultArtifactVersion(process);
                restrictions.add(Restriction.EVERYTHING);
            }

            return new VersionRange(version, restrictions);
        }
    }

    private static Restriction parseRestriction(String spec) throws InvalidVersionSpecificationException {
        boolean lowerBoundInclusive = spec.startsWith("[");
        boolean upperBoundInclusive = spec.endsWith("]");
        String process = spec.substring(1, spec.length() - 1).trim();
        int index = process.indexOf(",");
        Restriction restriction;
        if (index < 0) {
            if (!lowerBoundInclusive || !upperBoundInclusive) {
                throw new InvalidVersionSpecificationException("Single version must be surrounded by []: " + spec);
            }

            ArtifactVersion version = new DefaultArtifactVersion(process);
            restriction = new Restriction(version, lowerBoundInclusive, version, upperBoundInclusive);
        } else {
            String lowerBound = process.substring(0, index).trim();
            String upperBound = process.substring(index + 1).trim();
            if (lowerBound.equals(upperBound)) {
                throw new InvalidVersionSpecificationException("Range cannot have identical boundaries: " + spec);
            }

            ArtifactVersion lowerVersion = null;
            if (lowerBound.length() > 0) {
                lowerVersion = new DefaultArtifactVersion(lowerBound);
            }

            ArtifactVersion upperVersion = null;
            if (upperBound.length() > 0) {
                upperVersion = new DefaultArtifactVersion(upperBound);
            }

            if (upperVersion != null && lowerVersion != null && upperVersion.compareTo(lowerVersion) < 0) {
                throw new InvalidVersionSpecificationException("Range defies version ordering: " + spec);
            }

            restriction = new Restriction(lowerVersion, lowerBoundInclusive, upperVersion, upperBoundInclusive);
        }

        return restriction;
    }

    public static VersionRange createFromVersion(String version, ArtifactVersion existing) {
        List<Restriction> restrictions = Collections.emptyList();
        if (existing == null) {
            existing = new DefaultArtifactVersion(version);
        }

        return new VersionRange(existing, restrictions);
    }

    public VersionRange restrict(VersionRange restriction) {
        List<Restriction> r1 = this.restrictions;
        List<Restriction> r2 = restriction.restrictions;
        List<Restriction> restrictions;
        if (!r1.isEmpty() && !r2.isEmpty()) {
            restrictions = this.intersection(r1, r2);
        } else {
            restrictions = Collections.emptyList();
        }

        ArtifactVersion version = null;
        if (restrictions.size() > 0) {
            for(Restriction r : restrictions) {
                if (this.recommendedVersion != null && r.containsVersion(this.recommendedVersion)) {
                    version = this.recommendedVersion;
                    break;
                }

                if (version == null && restriction.getRecommendedVersion() != null && r.containsVersion(restriction.getRecommendedVersion())) {
                    version = restriction.getRecommendedVersion();
                }
            }
        } else if (this.recommendedVersion != null) {
            version = this.recommendedVersion;
        } else if (restriction.recommendedVersion != null) {
            version = restriction.recommendedVersion;
        }

        return new VersionRange(version, restrictions);
    }

    private List<Restriction> intersection(List<Restriction> r1, List<Restriction> r2) {
        List<Restriction> restrictions = new ArrayList(r1.size() + r2.size());
        Iterator<Restriction> i1 = r1.iterator();
        Iterator<Restriction> i2 = r2.iterator();
        Restriction res1 = (Restriction)i1.next();
        Restriction res2 = (Restriction)i2.next();
        boolean done = false;

        while(!done) {
            if (res1.getLowerBound() != null && res2.getUpperBound() != null && res1.getLowerBound().compareTo(res2.getUpperBound()) > 0) {
                if (i2.hasNext()) {
                    res2 = (Restriction)i2.next();
                } else {
                    done = true;
                }
            } else if (res1.getUpperBound() != null && res2.getLowerBound() != null && res1.getUpperBound().compareTo(res2.getLowerBound()) < 0) {
                if (i1.hasNext()) {
                    res1 = (Restriction)i1.next();
                } else {
                    done = true;
                }
            } else {
                ArtifactVersion lower;
                boolean lowerInclusive;
                if (res1.getLowerBound() == null) {
                    lower = res2.getLowerBound();
                    lowerInclusive = res2.isLowerBoundInclusive();
                } else if (res2.getLowerBound() == null) {
                    lower = res1.getLowerBound();
                    lowerInclusive = res1.isLowerBoundInclusive();
                } else {
                    int comparison = res1.getLowerBound().compareTo(res2.getLowerBound());
                    if (comparison < 0) {
                        lower = res2.getLowerBound();
                        lowerInclusive = res2.isLowerBoundInclusive();
                    } else if (comparison == 0) {
                        lower = res1.getLowerBound();
                        lowerInclusive = res1.isLowerBoundInclusive() && res2.isLowerBoundInclusive();
                    } else {
                        lower = res1.getLowerBound();
                        lowerInclusive = res1.isLowerBoundInclusive();
                    }
                }

                ArtifactVersion upper;
                boolean upperInclusive;
                if (res1.getUpperBound() == null) {
                    upper = res2.getUpperBound();
                    upperInclusive = res2.isUpperBoundInclusive();
                } else if (res2.getUpperBound() == null) {
                    upper = res1.getUpperBound();
                    upperInclusive = res1.isUpperBoundInclusive();
                } else {
                    int comparison = res1.getUpperBound().compareTo(res2.getUpperBound());
                    if (comparison < 0) {
                        upper = res1.getUpperBound();
                        upperInclusive = res1.isUpperBoundInclusive();
                    } else if (comparison == 0) {
                        upper = res1.getUpperBound();
                        upperInclusive = res1.isUpperBoundInclusive() && res2.isUpperBoundInclusive();
                    } else {
                        upper = res2.getUpperBound();
                        upperInclusive = res2.isUpperBoundInclusive();
                    }
                }

                if (lower == null || upper == null || lower.compareTo(upper) != 0) {
                    restrictions.add(new Restriction(lower, lowerInclusive, upper, upperInclusive));
                } else if (lowerInclusive && upperInclusive) {
                    restrictions.add(new Restriction(lower, lowerInclusive, upper, upperInclusive));
                }

                if (upper == res2.getUpperBound()) {
                    if (i2.hasNext()) {
                        res2 = (Restriction)i2.next();
                    } else {
                        done = true;
                    }
                } else if (i1.hasNext()) {
                    res1 = (Restriction)i1.next();
                } else {
                    done = true;
                }
            }
        }

        return restrictions;
    }

    public String toString() {
        return this.recommendedVersion != null ? this.recommendedVersion.toString() : Joiner.on(',').join(this.restrictions);
    }

    public ArtifactVersion matchVersion(List<ArtifactVersion> versions) {
        ArtifactVersion matched = null;

        for(ArtifactVersion version : versions) {
            if (this.containsVersion(version) && (matched == null || version.compareTo(matched) > 0)) {
                matched = version;
            }
        }

        return matched;
    }

    public boolean containsVersion(ArtifactVersion version) {
        for(Restriction restriction : this.restrictions) {
            if (restriction.containsVersion(version)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasRestrictions() {
        return !this.restrictions.isEmpty() && this.recommendedVersion == null;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof VersionRange)) {
            return false;
        } else {
            VersionRange other = (VersionRange)obj;
            boolean equals = this.recommendedVersion == other.recommendedVersion
                    || this.recommendedVersion != null && this.recommendedVersion.equals(other.recommendedVersion);
            return equals & (this.restrictions == other.restrictions || this.restrictions != null && this.restrictions.equals(other.restrictions));
        }
    }

    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.recommendedVersion == null ? 0 : this.recommendedVersion.hashCode());
        return 31 * hash + (this.restrictions == null ? 0 : this.restrictions.hashCode());
    }
}
