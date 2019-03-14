/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.datastorage;

import BioticTypes.v3.AgedeterminationType;
import BioticTypes.v3.CatchsampleType;
import BioticTypes.v3.CopepodedevstageType;
import BioticTypes.v3.FishstationType;
import BioticTypes.v3.IndividualType;
import BioticTypes.v3.MissionType;
import BioticTypes.v3.PreyType;
import BioticTypes.v3.PreylengthType;
import BioticTypes.v3.TagType;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import no.imr.sea2data.biotic.bo.BaseBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.stox.functions.utils.BioticUtils;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.sea2data.imrbase.util.ImrIO;
import no.imr.stox.functions.utils.Functions;

/**
 * TODO: what does this class do?
 *
 * @author aasmunds
 */
public class BioticDataStorage extends FileDataStorage {

    public BioticDataStorage() {
    }

    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits) {
        asTable((List<Object>) data, level, wr);
    }

    @Override
    public Integer getNumDataStorageFiles() {
        return 9;
    }

    @Override
    public String getStorageFileNamePostFix(Integer idxFile) {
        Class cls = getClass(idxFile);
        String str = cls.getSimpleName();
        return str.substring(0, str.length() - 4); // remove Type
    }

    private static Class getClass(Integer idx) {
        switch (idx) {
            case 1:
                return MissionType.class;
            case 2:
                return FishstationType.class;
            case 3:
                return CatchsampleType.class;
            case 4:
                return IndividualType.class;
            case 5:
                return AgedeterminationType.class;
            case 6:
                return TagType.class;
            case 7:
                return PreyType.class;
            case 8:
                return PreylengthType.class;
            case 9:
                return CopepodedevstageType.class;
        }
        return null;
    }

    public static void asTable(List<Object> list, Integer level, Writer wr) {
        // Old code
        List<MissionType> ml = ((List<MissionBO>) (List) list).stream().map(m -> m.bo()).collect(Collectors.toList()); // box missionbo to missiontype
        switch (level) {
            case 1:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                        BaseBO.csvHdr(MissionType.class, null, null))));
                for (MissionType ms : ml) {
                    ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                            BaseBO.csv(ms, null, null))));
                }
                break;
            case 2:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                        BaseBO.csvHdr(MissionType.class, null, true),
                        BaseBO.csvHdr(FishstationType.class, null, null))));
                for (MissionType ms : ml) {
                    for (FishstationType fs : ms.getFishstation()) {
                        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                                BaseBO.csv(ms, null, true),
                                BaseBO.csv(fs, null, null))));
                    }
                }
                break;
            case 3:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                        BaseBO.csvHdr(MissionType.class, null, true),
                        BaseBO.csvHdr(FishstationType.class, null, true),
                        BaseBO.csvHdr(CatchsampleType.class, null, null)
                )));
                for (MissionType ms : ml) {
                    for (FishstationType fs : ms.getFishstation()) {
                        for (CatchsampleType cs : fs.getCatchsample()) {
                            ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                                    BaseBO.csv(ms, null, true),
                                    BaseBO.csv(fs, null, true),
                                    BaseBO.csv(cs, null, null)
                            )));
                        }
                    }
                }
                break;
            case 4:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                        BaseBO.csvHdr(MissionType.class, null, true),
                        BaseBO.csvHdr(FishstationType.class, null, true),
                        BaseBO.csvHdr(CatchsampleType.class, null, true),
                        BaseBO.csvHdr(IndividualType.class, null, null)
                )));
                for (MissionType ms : ml) {
                    for (FishstationType fs : ms.getFishstation()) {
                        for (CatchsampleType cs : fs.getCatchsample()) {
                            for (IndividualType ii : cs.getIndividual()) {
                                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                                        BaseBO.csv(ms, null, true),
                                        BaseBO.csv(fs, null, true),
                                        BaseBO.csv(cs, null, true),
                                        BaseBO.csv(ii, null, null)
                                )));
                            }
                        }
                    }
                }
                break;
            case 5:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                        BaseBO.csvHdr(MissionType.class, null, true),
                        BaseBO.csvHdr(FishstationType.class, null, true),
                        BaseBO.csvHdr(CatchsampleType.class, null, true),
                        BaseBO.csvHdr(IndividualType.class, null, true),
                        BaseBO.csvHdr(AgedeterminationType.class, null, null)
                )));
                for (MissionType ms : ml) {
                    for (FishstationType fs : ms.getFishstation()) {
                        for (CatchsampleType cs : fs.getCatchsample()) {
                            for (IndividualType ii : cs.getIndividual()) {
                                for (AgedeterminationType a : ii.getAgedetermination()) {
                                    ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                                            BaseBO.csv(ms, null, true),
                                            BaseBO.csv(fs, null, true),
                                            BaseBO.csv(cs, null, true),
                                            BaseBO.csv(ii, null, true),
                                            BaseBO.csv(a, null, null)
                                    )));
                                }
                            }
                        }
                    }
                }
                break;
            case 6:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                        BaseBO.csvHdr(MissionType.class, null, true),
                        BaseBO.csvHdr(FishstationType.class, null, true),
                        BaseBO.csvHdr(CatchsampleType.class, null, true),
                        BaseBO.csvHdr(IndividualType.class, null, true),
                        BaseBO.csvHdr(TagType.class, null, null)
                )));
                for (MissionType ms : ml) {
                    for (FishstationType fs : ms.getFishstation()) {
                        for (CatchsampleType cs : fs.getCatchsample()) {
                            for (IndividualType ii : cs.getIndividual()) {
                                for (TagType t : ii.getTag()) {
                                    ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                                            BaseBO.csv(ms, null, true),
                                            BaseBO.csv(fs, null, true),
                                            BaseBO.csv(cs, null, true),
                                            BaseBO.csv(ii, null, true),
                                            BaseBO.csv(t, null, null)
                                    )));
                                }
                            }
                        }
                    }
                }
                break;
            case 7:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                        BaseBO.csvHdr(MissionType.class, null, true),
                        BaseBO.csvHdr(FishstationType.class, null, true),
                        BaseBO.csvHdr(CatchsampleType.class, null, true),
                        BaseBO.csvHdr(IndividualType.class, null, true),
                        BaseBO.csvHdr(PreyType.class, null, null)
                )));
                for (MissionType ms : ml) {
                    for (FishstationType fs : ms.getFishstation()) {
                        for (CatchsampleType cs : fs.getCatchsample()) {
                            for (IndividualType ii : cs.getIndividual()) {
                                for (PreyType p : ii.getPrey()) {
                                    ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                                            BaseBO.csv(ms, null, true),
                                            BaseBO.csv(fs, null, true),
                                            BaseBO.csv(cs, null, true),
                                            BaseBO.csv(ii, null, true),
                                            BaseBO.csv(p, null, null)
                                    )));
                                }
                            }
                        }
                    }
                }
                break;
            case 8:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                        BaseBO.csvHdr(MissionType.class, null, true),
                        BaseBO.csvHdr(FishstationType.class, null, true),
                        BaseBO.csvHdr(CatchsampleType.class, null, true),
                        BaseBO.csvHdr(IndividualType.class, null, true),
                        BaseBO.csvHdr(PreyType.class, null, true),
                        BaseBO.csvHdr(PreylengthType.class, null, null)
                )));
                for (MissionType ms : ml) {
                    for (FishstationType fs : ms.getFishstation()) {
                        for (CatchsampleType cs : fs.getCatchsample()) {
                            for (IndividualType ii : cs.getIndividual()) {
                                for (PreyType p : ii.getPrey()) {
                                    for (PreylengthType pl : p.getPreylengthfrequencytable()) {
                                        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                                                BaseBO.csv(ms, null, true),
                                                BaseBO.csv(fs, null, true),
                                                BaseBO.csv(cs, null, true),
                                                BaseBO.csv(ii, null, true),
                                                BaseBO.csv(p, null, true),
                                                BaseBO.csv(pl, null, null)
                                        )));
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case 9:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                        BaseBO.csvHdr(MissionType.class, null, true),
                        BaseBO.csvHdr(FishstationType.class, null, true),
                        BaseBO.csvHdr(CatchsampleType.class, null, true),
                        BaseBO.csvHdr(IndividualType.class, null, true),
                        BaseBO.csvHdr(PreyType.class, null, true),
                        BaseBO.csvHdr(CopepodedevstageType.class, null, null)
                )));
                for (MissionType ms : ml) {
                    for (FishstationType fs : ms.getFishstation()) {
                        for (CatchsampleType cs : fs.getCatchsample()) {
                            for (IndividualType ii : cs.getIndividual()) {
                                for (PreyType p : ii.getPrey()) {
                                    for (CopepodedevstageType cd : p.getCopepodedevstagefrequencytable()) {
                                        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                                                BaseBO.csv(ms, null, true),
                                                BaseBO.csv(fs, null, true),
                                                BaseBO.csv(cs, null, true),
                                                BaseBO.csv(ii, null, true),
                                                BaseBO.csv(p, null, true),
                                                BaseBO.csv(cd, null, null)
                                        )));
                                    }
                                }
                            }
                        }
                    }
                }
                break;
        }
    }

    /**
     *
     * @param context
     * @param inds
     * @param wr
     */
    public static void asTable(String context, List<IndividualBO> inds, Writer wr) {
        List<String> fields = new ArrayList<>(Functions.INDIVIDUALS);
//        fields.add("comment");
        asTable(fields, context, inds, wr);
    }

    public static void asTable(List<String> fields, String context, List<IndividualBO> inds, Writer wr) {
        for (IndividualBO i : inds) {
            String line = context;
            for (String code : fields) {
                Object c = BioticUtils.getIndVar(i, code);
                line = line != null ? ExportUtil.tabbed(line, c) : c == null ? "-" : c.toString();
            }
            // Add comment
            //line = line != null ? ExportUtil.tabbed(line, i.getCatchcomment()) : line;

            line = ExportUtil.carrageReturnLineFeed(line);
            ImrIO.write(wr, line);
        }
    }
}
