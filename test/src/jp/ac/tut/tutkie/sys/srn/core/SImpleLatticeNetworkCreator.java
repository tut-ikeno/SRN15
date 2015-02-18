/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.tut.tutkie.sys.srn.core;

import java.awt.Dimension;
import java.awt.Point;
import java.util.List;

/**
 *
 * @author tokumitsu
 */
public class SImpleLatticeNetworkCreator implements NetworkCreator {
    
    private List<Agent> list_ = null;
    private Dimension dimension_ = null;
    
    public SImpleLatticeNetworkCreator(List<Agent> list, Dimension dimension) {
        this.list_ = list;
        this.dimension_ = dimension;
    }

    public void create() {
        Point p = new Point();
        
        List<Agent> list = getUnitList();
        Dimension dimension = getDimension();
        
        for(Agent unit: list) {
            Point point = unit.getPoint();
            
            int width = 3;
            int height = 3;
            
            for(int i = 0; i < height; i++) {
                for(int j = 0; j < width; j++) {
                    p.x = point.x - 1 + j;
                    p.y = point.y - 1 + i;
                    
                    p.x = p.x % dimension.width;
                    p.y = p.y % dimension.height;
                    
                    if(point.equals(p)) {
                        continue; /* 自分はスキップ */
                    }
                    
                    for (Agent neighborUnit : list) {
                        Point neighborUnitPoint = neighborUnit.getPoint();

                        if (neighborUnitPoint.equals(p)) {
                            unit.addNeighborUnit(neighborUnit);
                            break;
                        }
                    }
                }
            }
        }

    }
    
    public List<Agent> getUnitList() {
        return list_;
    }

    public Dimension getDimension() {
        return dimension_;
    }
}
