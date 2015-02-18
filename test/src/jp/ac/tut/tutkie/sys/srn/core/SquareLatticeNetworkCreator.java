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
public class SquareLatticeNetworkCreator implements NetworkCreator {
    
    private List<Agent> list_ = null;
    private Dimension dimension_ = null;
    private int radius_ = 0;
    
    public SquareLatticeNetworkCreator(List<Agent> list, Dimension dimension, int radius) {
        this.list_ = list;
        this.dimension_ = dimension;
        this.radius_ = radius;
    }

    public void create() {
        Point p = new Point();
        
        List<Agent> list = getUnitList();

        Dimension d = getDimension();
        int r = getRadius();
        
        for(Agent unit: list) {
            Point point = unit.getPoint();

            for(int x = point.x - r; x <= point.x + r; x++) {
                for(int y = point.y - r; y <= point.y + r; y++) {
                    p.x = x;
                    p.y = y;
                    
                    if(p.x < 0) {
                        p.x = d.width + p.x;
                    }
                    if(x >= d.width) {
                        p.x = d.width - p.x;
                    }
                    if(p.y < 0) {
                        p.y = d.height + p.y;
                    }
                    if(p.y >= d.height) {
                        p.y = d.height - p.y;
                    }

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

    public int getRadius() {
        return radius_;
    }
    
    public List<Agent> getUnitList() {
        return list_;
    }

    public Dimension getDimension() {
        return dimension_;
    }
    
}
