function move(deltaX, deltaY, deltaZ)
    local position = get(Position.class)
    
    position:setX(position:getX() + deltaX)
    position:setY(position:getY() + deltaY)
    position:setZ(position:getZ() + deltaZ)
end